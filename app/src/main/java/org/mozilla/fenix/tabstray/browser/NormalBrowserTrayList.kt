/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.tabstray.browser

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.ConcatAdapter
import mozilla.components.browser.state.state.TabSessionState
import mozilla.components.browser.tabstray.TabViewHolder
import mozilla.components.feature.tabs.tabstray.TabsFeature
import org.mozilla.fenix.ext.components
import org.mozilla.fenix.ext.settings
import org.mozilla.fenix.tabstray.ext.browserAdapter
import org.mozilla.fenix.tabstray.ext.inactiveTabsAdapter
import org.mozilla.fenix.tabstray.ext.isNormalTabInactive
import java.util.concurrent.TimeUnit

/**
 * The time until which a tab is considered in-active (in days).
 */
const val DEFAULT_ACTIVE_DAYS = 14L

/**
 * The maximum time from when a tab was created or accessed until it is considered "inactive".
 */
val maxActiveTime = TimeUnit.DAYS.toMillis(DEFAULT_ACTIVE_DAYS)

class NormalBrowserTrayList @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AbstractBrowserTrayList(context, attrs, defStyleAttr) {

    private val concatAdapter by lazy { adapter as ConcatAdapter }
    private val tabSorter by lazy {
        TabSorter(
            context.settings(),
            context.components.analytics.metrics,
            concatAdapter
        )
    }
    private val inactiveTabsFilter: (TabSessionState) -> Boolean = filter@{
        if (!context.settings().inactiveTabsAreEnabled) {
            return@filter false
        }
        it.isNormalTabInactive(maxActiveTime)
    }

    private val inactiveTabsInteractor by lazy {
        DefaultInactiveTabsInteractor(
            InactiveTabsController(
                context.components.core.store,
                inactiveTabsFilter,
                concatAdapter.inactiveTabsAdapter,
                context.components.analytics.metrics
            )
        )
    }

    private val inactiveTabsAutoCloseInteractor by lazy {
        DefaultInactiveTabsAutoCloseDialogInteractor(
            InactiveTabsAutoCloseDialogController(
                context.components.core.store,
                context.settings(),
                inactiveTabsFilter,
                concatAdapter.inactiveTabsAdapter
            )
        )
    }

    override val tabsFeature by lazy {
        TabsFeature(
            tabSorter,
            context.components.core.store,
            { !it.content.private },
        )
    }

    private val touchHelper by lazy {
        TabsTouchHelper(
            interactionDelegate = concatAdapter.browserAdapter.interactor,
            onViewHolderTouched = {
                it is TabViewHolder && swipeToDelete.isSwipeable
            },
            onViewHolderDraw = { context.components.settings.gridTabView.not() },
            featureNameHolder = concatAdapter.browserAdapter
        )
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        concatAdapter.inactiveTabsAdapter.inactiveTabsInteractor = inactiveTabsInteractor
        concatAdapter.inactiveTabsAdapter.inactiveTabsAutoCloseDialogInteractor = inactiveTabsAutoCloseInteractor

        tabsFeature.start()

        touchHelper.attachToRecyclerView(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        tabsFeature.stop()

        touchHelper.attachToRecyclerView(null)
    }
}
