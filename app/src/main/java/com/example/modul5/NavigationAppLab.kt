package com.example.modul5

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.modul5.data.local.DatabaseProvider
import com.example.modul5.data.preferences.UserPreferencesRepository
import com.example.modul5.data.remote.RetrofitClient
import com.example.modul5.data.remote.StudentRemoteDataSource
import com.example.modul5.data.repository.DefaultStudentRepository
import com.example.modul5.data.repository.FavoriteRepository
import com.example.modul5.screen.AboutScreen
import com.example.modul5.screen.DetailScreen
import com.example.modul5.screen.HomeScreen
import com.example.modul5.screen.ProfileScreen
import com.example.modul5.screen.FavoritesScreen
import com.example.modul5.ui.favorites.FavoritesViewModel
import com.example.modul5.ui.detail.DetailViewModel
import com.example.modul5.ui.home.HomeViewModel

@Composable
fun NavigationAppLab() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val remoteDataSource = remember { StudentRemoteDataSource(RetrofitClient.apiService) }
    val repository = remember { DefaultStudentRepository(remoteDataSource) }
    val preferencesRepository = remember { UserPreferencesRepository(context.applicationContext) }

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.factory(repository, preferencesRepository)
            )
            val homeUiState by homeViewModel.uiState.collectAsStateWithLifecycle()
            val searchQuery by homeViewModel.searchQuery.collectAsStateWithLifecycle()

            HomeScreen(
                uiState = homeUiState,
                searchQuery = searchQuery,
                onSearchQueryChange = { query ->
                    homeViewModel.onSearchQueryChange(query)
                },
                onStudentClick = { id ->
                    navController.navigate(Routes.detail(id))
                },
                onOpenProfile = {
                    navController.navigate(Routes.PROFILE)
                },
                onOpenAbout = {
                    navController.navigate(Routes.ABOUT)
                },
                onOpenFavorites = {
                    navController.navigate(Routes.FAVORITES)
                },
                onRetry = {
                    homeViewModel.retry()
                },
                onRefresh = {
                    homeViewModel.refresh()
                }
            )
        }
        composable(
            route = Routes.DETAIL,
            arguments = listOf(
                navArgument("studentId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getInt("studentId") ?: 0
            val database = remember { DatabaseProvider.getDatabase(context) }
            val favoriteRepository = remember { FavoriteRepository(database.favoriteStudentDao()) }

            val viewModel: DetailViewModel = viewModel(
                factory = DetailViewModel.factory(repository, favoriteRepository)
            )

            LaunchedEffect(studentId) {
                viewModel.loadStudent(studentId)
            }

            val detailUiState by viewModel.uiState.collectAsStateWithLifecycle()

            DetailScreen(
                uiState = detailUiState,
                onToggleFavorite = { viewModel.toggleFavorite() },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.PROFILE) {
            ProfileScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.ABOUT) {
            AboutScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.FAVORITES) {
            val database = remember { DatabaseProvider.getDatabase(context) }
            val favoriteRepository = remember { FavoriteRepository(database.favoriteStudentDao()) }

            val favoritesViewModel: FavoritesViewModel = viewModel(
                factory = FavoritesViewModel.factory(favoriteRepository)
            )
            val favorites by favoritesViewModel.favorites.collectAsStateWithLifecycle()

            FavoritesScreen(
                favorites = favorites,
                onStudentClick = { id ->
                    navController.navigate(Routes.detail(id))
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}