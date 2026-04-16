package com.example.reader.presentation

import android.view.View
import android.widget.FrameLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.readium.r2.navigator.epub.EpubNavigatorFactory
import org.readium.r2.navigator.epub.EpubNavigatorFragment
import org.readium.r2.navigator.epub.EpubPreferences
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.publication.Locator
import org.readium.r2.shared.publication.Publication

@Composable
fun FragmentActivity.EpubReaderScreen(
    modifier: Modifier = Modifier,
    viewModel: EpubReaderViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.onLoadBook("ebook.demo.epub")
    }

    val state = viewModel.state.collectAsStateWithLifecycle()

    when {
        state.value.isLoading -> {
            EpubReaderScreenLoading(
                modifier = modifier,
            )
        }
        state.value.isError -> {
            EpubReaderScreenError(
                modifier = modifier,
            )
        }
        state.value.publication != null -> {
            EpubReaderScreenContent(
                modifier = modifier,
                publication = state.value.publication ?: return,
                onPageChanged = viewModel::onPageChanged,
                initialLocator = state.value.initialLocator,
                progress = state.value.progress,
            )
        }
        else -> {
            EpubReaderScreenError(
                modifier = modifier,
            )
        }
    }

}

@Composable
fun EpubReaderScreenError(
    modifier: Modifier,
    errorText: String = "Произошла ошибка",
) {
    Box(modifier = modifier.fillMaxSize()) {
        Text(
            text = errorText,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun EpubReaderScreenLoading(
    modifier: Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@OptIn(ExperimentalReadiumApi::class)
@Composable
private fun FragmentActivity.EpubReaderScreenContent(
    modifier: Modifier = Modifier,
    publication: Publication,
    initialLocator: Locator?,
    progress: Double,
    onPageChanged: (Locator) -> Unit,
) {
    val navigatorFactory = remember(publication) {
        EpubNavigatorFactory(publication)
    }
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AndroidView(
            modifier = Modifier.weight(1f),
            factory = { context ->
                FragmentContainerView(context).apply {
                    id = View.generateViewId()
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT,
                    )
                }
            },
            update = { view ->
                val fragmentManager = supportFragmentManager
                fragmentManager.fragmentFactory = navigatorFactory.createFragmentFactory(
                    initialLocator = initialLocator,
                    paginationListener = object : EpubNavigatorFragment.PaginationListener {
                        override fun onPageChanged(
                            pageIndex: Int,
                            totalPages: Int,
                            locator: Locator
                        ) {
                            super.onPageChanged(pageIndex, totalPages, locator)
                            onPageChanged(locator)
                        }
                    },
                    initialPreferences = EpubPreferences(
                        scroll = true
                    ),
                    configuration = EpubNavigatorFragment.Configuration.invoke {
                        disablePageTurnsWhileScrolling = false
                        shouldApplyInsetsPadding = true
                    }
                )
                val fragment = fragmentManager.fragmentFactory.instantiate(
                    classLoader,
                    EpubNavigatorFragment::class.java.name
                )
                if (fragmentManager.findFragmentById(view.id) == null) {
                    fragmentManager.commit {
                        replace(view.id, fragment)
                    }
                }
            }
        )
        LinearProgressIndicator(
            progress = { progress.toFloat() },
            modifier = Modifier.padding(top = 8.dp).fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}