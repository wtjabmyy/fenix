/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.home

import io.mockk.mockk
import io.mockk.verify
import mozilla.components.concept.storage.BookmarkNode
import mozilla.components.concept.storage.BookmarkNodeType
import mozilla.components.feature.tab.collections.Tab
import mozilla.components.feature.tab.collections.TabCollection
import mozilla.components.service.pocket.PocketRecommendedStory
import org.junit.Before
import org.junit.Test
import org.mozilla.fenix.browser.browsingmode.BrowsingMode
import org.mozilla.fenix.historymetadata.controller.HistoryMetadataController
import org.mozilla.fenix.home.recentbookmarks.controller.RecentBookmarksController
import org.mozilla.fenix.home.recenttabs.controller.RecentTabController
import org.mozilla.fenix.home.sessioncontrol.DefaultSessionControlController
import org.mozilla.fenix.home.sessioncontrol.SessionControlInteractor
import org.mozilla.fenix.home.pocket.PocketRecommendedStoriesCategory
import org.mozilla.fenix.home.pocket.PocketStoriesController

class SessionControlInteractorTest {

    private val controller: DefaultSessionControlController = mockk(relaxed = true)
    private val recentTabController: RecentTabController = mockk(relaxed = true)
    private val recentBookmarksController: RecentBookmarksController = mockk(relaxed = true)
    private val pocketStoriesController: PocketStoriesController = mockk(relaxed = true)

    // Note: the historyMetadata tests are handled in [HistoryMetadataInteractorTest] and [HistoryMetadataControllerTest]
    private val historyMetadataController: HistoryMetadataController = mockk(relaxed = true)

    private lateinit var interactor: SessionControlInteractor

    @Before
    fun setup() {
        interactor = SessionControlInteractor(
            controller,
            recentTabController,
            recentBookmarksController,
            historyMetadataController,
            pocketStoriesController
        )
    }

    @Test
    fun onCollectionAddTabTapped() {
        val collection: TabCollection = mockk(relaxed = true)
        interactor.onCollectionAddTabTapped(collection)
        verify { controller.handleCollectionAddTabTapped(collection) }
    }

    @Test
    fun onCollectionOpenTabClicked() {
        val tab: Tab = mockk(relaxed = true)
        interactor.onCollectionOpenTabClicked(tab)
        verify { controller.handleCollectionOpenTabClicked(tab) }
    }

    @Test
    fun onCollectionOpenTabsTapped() {
        val collection: TabCollection = mockk(relaxed = true)
        interactor.onCollectionOpenTabsTapped(collection)
        verify { controller.handleCollectionOpenTabsTapped(collection) }
    }

    @Test
    fun onCollectionRemoveTab() {
        val collection: TabCollection = mockk(relaxed = true)
        val tab: Tab = mockk(relaxed = true)
        interactor.onCollectionRemoveTab(collection, tab, false)
        verify { controller.handleCollectionRemoveTab(collection, tab, false) }
    }

    @Test
    fun onCollectionShareTabsClicked() {
        val collection: TabCollection = mockk(relaxed = true)
        interactor.onCollectionShareTabsClicked(collection)
        verify { controller.handleCollectionShareTabsClicked(collection) }
    }

    @Test
    fun onDeleteCollectionTapped() {
        val collection: TabCollection = mockk(relaxed = true)
        interactor.onDeleteCollectionTapped(collection)
        verify { controller.handleDeleteCollectionTapped(collection) }
    }

    @Test
    fun onPrivateBrowsingLearnMoreClicked() {
        interactor.onPrivateBrowsingLearnMoreClicked()
        verify { controller.handlePrivateBrowsingLearnMoreClicked() }
    }

    @Test
    fun onRenameCollectionTapped() {
        val collection: TabCollection = mockk(relaxed = true)
        interactor.onRenameCollectionTapped(collection)
        verify { controller.handleRenameCollectionTapped(collection) }
    }

    @Test
    fun onStartBrowsingClicked() {
        interactor.onStartBrowsingClicked()
        verify { controller.handleStartBrowsingClicked() }
    }

    @Test
    fun onToggleCollectionExpanded() {
        val collection: TabCollection = mockk(relaxed = true)
        interactor.onToggleCollectionExpanded(collection, true)
        verify { controller.handleToggleCollectionExpanded(collection, true) }
    }

    @Test
    fun onAddTabsToCollection() {
        interactor.onAddTabsToCollectionTapped()
        verify { controller.handleCreateCollection() }
    }

    @Test
    fun onPaste() {
        interactor.onPaste("text")
        verify { controller.handlePaste("text") }
    }

    @Test
    fun onPasteAndGo() {
        interactor.onPasteAndGo("text")
        verify { controller.handlePasteAndGo("text") }
    }

    @Test
    fun onRemoveCollectionsPlaceholder() {
        interactor.onRemoveCollectionsPlaceholder()
        verify { controller.handleRemoveCollectionsPlaceholder() }
    }

    @Test
    fun onCollectionMenuOpened() {
        interactor.onCollectionMenuOpened()
        verify { controller.handleMenuOpened() }
    }

    @Test
    fun onTopSiteMenuOpened() {
        interactor.onTopSiteMenuOpened()
        verify { controller.handleMenuOpened() }
    }

    @Test
    fun onRecentTabClicked() {
        val tabId = "tabId"
        interactor.onRecentTabClicked(tabId)
        verify { recentTabController.handleRecentTabClicked(tabId) }
    }

    @Test
    fun onRecentSearchGroupClicked() {
        val tabId = "tabId"
        interactor.onRecentSearchGroupClicked(tabId)
        verify { recentTabController.handleRecentSearchGroupClicked(tabId) }
    }

    @Test
    fun onRecentTabShowAllClicked() {
        interactor.onRecentTabShowAllClicked()
        verify { recentTabController.handleRecentTabShowAllClicked() }
    }

    @Test
    fun `WHEN a recently saved bookmark is clicked THEN the selected bookmark is handled`() {
        val bookmark = BookmarkNode(
            type = BookmarkNodeType.ITEM,
            guid = "guid#${Math.random() * 1000}",
            parentGuid = null,
            position = null,
            title = null,
            url = null,
            dateAdded = 0,
            children = null
        )

        interactor.onRecentBookmarkClicked(bookmark)
        verify { recentBookmarksController.handleBookmarkClicked(bookmark) }
    }

    @Test
    fun `WHEN tapping on the customize home button THEN openCustomizeHomePage`() {
        interactor.openCustomizeHomePage()
        verify { controller.handleCustomizeHomeTapped() }
    }

    @Test
    fun `WHEN calling showOnboardingDialog THEN handleShowOnboardingDialog`() {
        interactor.showOnboardingDialog()
        verify { controller.handleShowOnboardingDialog() }
    }

    @Test
    fun `WHEN Show All recently saved bookmarks button is clicked THEN the click is handled`() {
        interactor.onShowAllBookmarksClicked()
        verify { recentBookmarksController.handleShowAllBookmarksClicked() }
    }

    @Test
    fun `WHEN private mode button is clicked THEN the click is handled`() {
        val newMode = BrowsingMode.Private
        val hasBeenOnboarded = true

        interactor.onPrivateModeButtonClicked(newMode, hasBeenOnboarded)
        verify { controller.handlePrivateModeButtonClicked(newMode, hasBeenOnboarded) }
    }

    @Test
    fun `GIVEN a PocketStoriesInteractor WHEN stories are shown THEN handle it in a PocketStoriesController`() {
        val shownStories: List<PocketRecommendedStory> = mockk()

        interactor.onStoriesShown(shownStories)

        verify { pocketStoriesController.handleStoriesShown(shownStories) }
    }

    @Test
    fun `GIVEN a PocketStoriesInteractor WHEN a category is clicked THEN handle it in a PocketStoriesController`() {
        val clickedCategory: PocketRecommendedStoriesCategory = mockk()

        interactor.onCategoryClicked(clickedCategory)

        verify { pocketStoriesController.handleCategoryClick(clickedCategory) }
    }

    @Test
    fun `GIVEN a PocketStoriesInteractor WHEN a story is clicked THEN handle it in a PocketStoriesController`() {
        val clickedStory: PocketRecommendedStory = mockk()
        val storyGridLocation = 1 to 2

        interactor.onStoryClicked(clickedStory, storyGridLocation)

        verify { pocketStoriesController.handleStoryClicked(clickedStory, storyGridLocation) }
    }

    @Test
    fun `GIVEN a PocketStoriesInteractor WHEN discover more clicked THEN handle it in a PocketStoriesController`() {
        val link = "http://getpocket.com/explore"

        interactor.onDiscoverMoreClicked(link)

        verify { pocketStoriesController.handleDiscoverMoreClicked(link) }
    }

    @Test
    fun `GIVEN a PocketStoriesInteractor WHEN learn more clicked THEN handle it in a PocketStoriesController`() {
        val link = "https://www.mozilla.org/en-US/firefox/pocket/"

        interactor.onLearnMoreClicked(link)

        verify { pocketStoriesController.handleLearnMoreClicked(link) }
    }
}
