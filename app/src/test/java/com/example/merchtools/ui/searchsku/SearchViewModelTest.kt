package com.example.merchtools.ui.searchsku

import androidx.lifecycle.SavedStateHandle
import com.example.merchtools.MainDispatcherRule
import com.example.merchtools.domain.model.Sku
import com.example.merchtools.domain.repository.SkuRepository
import com.example.merchtools.domain.use_case.AddSkuUseCase
import com.example.merchtools.domain.use_case.SearchSkuUseCase
import com.example.merchtools.core.Resource
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    lateinit var skuRepository: SkuRepository
    @Mock
    lateinit var addSkuUseCase: AddSkuUseCase
    @Mock
    lateinit var searchSkuUseCase: SearchSkuUseCase
    private lateinit var viewModel: SearchViewModel


    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        whenever(skuRepository.getAllSkusStream())
            .thenReturn(flowOf(Resource.Success(emptyList())))

        val savedStateHandle = SavedStateHandle()

        viewModel = SearchViewModel(
            addSkuUseCase = addSkuUseCase,
            skuRepository = skuRepository,
            searchSkuUseCase = searchSkuUseCase
        )
    }


    @Test
    fun `successful search updates with results`() = runTest {
        val query = "Pepsi".lowercase()
        val skus = listOf(
            Sku(
                skuId = 1L,
                name = "1234",
                brand = "Pepsi",
                upc = "123456789012",
                casePack = "12pk",
                imageUri = null
            )
        )

        whenever(searchSkuUseCase.catalog(query = query))
            .thenReturn(flowOf(Resource.Success(skus)))

        // Act: simulate user typing + pressing search
        viewModel.onEvent(SearchSkuEvent.OnSearchQueryChange(query))

        advanceTimeBy(500)
        advanceUntilIdle()

        // Assert: check if state is updated with results
        assertEquals(skus, viewModel.state.skus)
        assertEquals(false, viewModel.state.isLoading)
        assertEquals(null, viewModel.state.error)

    }

    @Test
    fun `search error updates state with error`() = runTest {
        val query = "Pepsi".lowercase()
        val errorMessage = "No result found"

        whenever(searchSkuUseCase.catalog(query = query))
            .thenReturn(flowOf(Resource.Error(errorMessage)))

        viewModel.onEvent(SearchSkuEvent.OnSearchQueryChange(query))

        advanceTimeBy(500)
        advanceUntilIdle()

        assertEquals(errorMessage, viewModel.state.error)
        assertEquals(emptyList<Sku>(), viewModel.state.skus)
        assertEquals(false, viewModel.state.isLoading)
    }
}