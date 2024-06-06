package ru.stersh.youamp.feature.albums.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import ru.stersh.youamp.core.ui.AlbumItem
import ru.stersh.youamp.core.ui.AlbumUi
import ru.stersh.youamp.core.ui.ErrorLayout
import ru.stersh.youamp.core.ui.OnBottomReached
import ru.stersh.youamp.core.ui.SkeletonLayout
import ru.stersh.youamp.core.ui.YouAmpPlayerTheme


@Composable
fun AlbumsScreen(
    viewModelStoreOwner: ViewModelStoreOwner,
    onAlbumClick: (id: String) -> Unit
) {
    val viewModel: AlbumsViewModel = koinViewModel(viewModelStoreOwner = viewModelStoreOwner)

    val state by viewModel.state.collectAsStateWithLifecycle()

    AlbumsScreen(
        state = state,
        onRefresh = viewModel::refresh,
        onRetry = viewModel::retry,
        onBottomReached = viewModel::loadMore,
        onAlbumClick = onAlbumClick
    )
}

@Composable
private fun AlbumsScreen(
    state: AlbumsStateUi,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
    onBottomReached: () -> Unit,
    onAlbumClick: (id: String) -> Unit
) {
    val pullRefreshState = rememberPullToRefreshState()

    if (pullRefreshState.isRefreshing) {
        onRefresh()
    }

    if (pullRefreshState.isRefreshing && !state.isRefreshing) {
        pullRefreshState.endRefresh()
    }

    val listState = rememberLazyGridState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(pullRefreshState.nestedScrollConnection)
    ) {
        when {
            state.progress -> {
                Progress(
                    listState = listState
                )
            }

            state.error -> {
                ErrorLayout(onRetry = onRetry)
            }

            state.items.isNotEmpty() -> {
                Content(
                    listState = listState,
                    state = state,
                    onAlbumClick = onAlbumClick
                )
            }
        }

        PullToRefreshContainer(
            modifier = Modifier.align(Alignment.TopCenter),
            state = pullRefreshState,
        )

        listState.OnBottomReached {
            onBottomReached()
        }
    }
}

@Composable
private fun Content(
    listState: LazyGridState,
    state: AlbumsStateUi,
    onAlbumClick: (id: String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = listState,
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    )
    {
        items(
            key = { it.id },
            items = state.items
        ) { album ->
            AlbumItem(
                album = album,
                onAlbumClick = onAlbumClick
            )
        }
    }
}

@Composable
private fun Progress(listState: LazyGridState) {
    SkeletonLayout {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            state = listState,
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                key = { it },
                items = (0..10).toList()
            ) {
                SkeletonItem(
                    modifier = Modifier.height(240.dp)
                )
            }
        }
    }
}

@Composable
@Preview
private fun AlbumsScreenPreview() {
    YouAmpPlayerTheme {
        val items = listOf(
            AlbumUi(
                id = "1",
                title = "Test",
                artist = "Test artist",
                artworkUrl = null
            ),
            AlbumUi(
                id = "2",
                title = "Test 2",
                artist = "Test artist 2 ",
                artworkUrl = null
            ),
            AlbumUi(
                id = "3",
                title = "Test 3",
                artist = "Test artist 3",
                artworkUrl = null
            )
        )
        val state = AlbumsStateUi(
            progress = true,
            isRefreshing = false,
            error = false,
            items = items
        )
        AlbumsScreen(
            state = state,
            onRefresh = {},
            onRetry = {},
            onBottomReached = {},
            onAlbumClick = {}
        )
    }
}