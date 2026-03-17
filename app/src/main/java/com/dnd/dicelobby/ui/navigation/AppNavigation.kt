package com.dnd.dicelobby.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dnd.dicelobby.ui.screens.*
import com.dnd.dicelobby.dice.DiceType
import com.dnd.dicelobby.ui.viewmodels.DiceViewModel
import com.dnd.dicelobby.ui.viewmodels.HomeViewModel
import com.dnd.dicelobby.ui.viewmodels.LobbyViewModel

/** Named route strings used throughout the navigation graph. */
object Routes {
    const val HOME               = "home"
    const val CHARACTER_SHEET    = "character_sheet"
    const val CHARACTER_CREATION = "character_creation"
    const val CREATE_LOBBY       = "create_lobby"
    const val JOIN_LOBBY         = "join_lobby"
    const val LOBBY              = "lobby"
    const val DICE               = "dice"
    const val DICE_SHEET         = "dice_sheet"
}

/**
 * Top-level Compose navigation graph.
 *
 * ViewModels are scoped to the NavBackStackEntry where appropriate so they survive
 * navigation within the lobby but are recreated when the user returns to the home screen.
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Shared ViewModels at nav-graph scope for lobby + dice screens
    val lobbyViewModel: LobbyViewModel = viewModel()
    val diceViewModel: DiceViewModel   = viewModel()
    val homeViewModel: HomeViewModel   = viewModel()

    NavHost(navController = navController, startDestination = Routes.HOME) {

        composable(Routes.HOME) {
            HomeScreen(
                homeViewModel   = homeViewModel,
                onChooseRace    = { navController.navigate(Routes.CHARACTER_SHEET) },
                onCreateLobby   = { navController.navigate(Routes.CREATE_LOBBY) },
                onJoinLobby     = { navController.navigate(Routes.JOIN_LOBBY) }
            )
        }

        composable(Routes.CHARACTER_SHEET) {
            CharacterSheetScreen(
                homeViewModel = homeViewModel,
                onBack        = { navController.popBackStack() }
            )
        }

        composable(Routes.CHARACTER_CREATION) {
            CharacterCreationScreen(
                homeViewModel = homeViewModel,
                onBack        = { navController.popBackStack() }
            )
        }

        composable(Routes.CREATE_LOBBY) {
            CreateLobbyScreen(
                homeViewModel = homeViewModel,
                lobbyViewModel = lobbyViewModel,
                onLobbyCreated = {
                    navController.navigate(Routes.LOBBY) {
                        popUpTo(Routes.HOME) { inclusive = false }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.JOIN_LOBBY) {
            JoinLobbyScreen(
                homeViewModel  = homeViewModel,
                lobbyViewModel = lobbyViewModel,
                onJoined = {
                    navController.navigate(Routes.LOBBY) {
                        popUpTo(Routes.HOME) { inclusive = false }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.LOBBY) {
            LobbyScreen(
                lobbyViewModel = lobbyViewModel,
                onStartRolling = { navController.navigate(Routes.DICE) },
                onLeave = {
                    lobbyViewModel.leaveLobby()
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.DICE) {
            DiceScreen(
                lobbyViewModel = lobbyViewModel,
                diceViewModel  = diceViewModel,
                onLeave = {
                    lobbyViewModel.leaveLobby()
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                onOpenSheet = { navController.navigate(Routes.DICE_SHEET) }
            )
        }

        // Character sheet opened from within the Dice screen — skills are tappable rolls
        composable(Routes.DICE_SHEET) {
            CharacterSheetScreen(
                homeViewModel = homeViewModel,
                onBack        = { navController.popBackStack() },
                onSkillRoll   = { modifier, label ->
                    // Pre-configure the dice VM then pop back so the overlay shows in DiceScreen
                    diceViewModel.selectDice(DiceType.D20)
                    diceViewModel.setDiceCount(1)
                    diceViewModel.setModifier(modifier)
                    diceViewModel.setCustomFormula("")
                    navController.popBackStack()
                    diceViewModel.startRoll()
                }
            )
        }
    }
}
