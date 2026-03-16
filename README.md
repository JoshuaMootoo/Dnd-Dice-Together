# DnD Dice Lobby

An Android application for tabletop RPG players to roll dice together on a local WiFi network.

## Features

- **Local Multiplayer Lobby** — up to 8 players on the same WiFi, connected via TCP sockets
- **3D Dice Physics** — OpenGL ES 2.0 renderer with a custom Euler physics engine; dice bounce, spin, and settle realistically on a virtual table
- **All Standard Dice** — d4, d6, d8, d10, d12, d20, d100
- **Dice Formula Parser** — supports `2d6+3`, `4d6kh3` (keep highest 3), `dl1` (drop lowest), and more
- **DM Hidden Rolls** — the Dungeon Master can roll secretly; other players see "DM rolled (hidden)"; the DM can reveal at any time
- **Shared Roll Log** — chronological log of all rolls with expandable detail (individual die values)
- **Local Persistence** — roll history stored in a Room SQLite database
- **Dark Fantasy UI** — deep-purple / crimson / gold colour scheme built with Jetpack Compose Material 3
- **Player Colour Customisation** — each player picks a colour that tints their dice

## Architecture

```
MVVM  +  Repository  +  Room  +  Kotlin Coroutines
```

| Layer | Technology |
|---|---|
| UI | Jetpack Compose + Material 3 |
| Navigation | Navigation Compose |
| ViewModel | androidx.lifecycle ViewModel |
| Networking | Raw TCP sockets (ServerSocket / Socket) |
| Serialization | Kotlinx Serialization JSON |
| Database | Room |
| Physics | Custom Euler integrator (pure Kotlin) |
| 3D Rendering | OpenGL ES 2.0 via GLSurfaceView |
| Preferences | DataStore |

## Module Layout

```
com.dnd.dicelobby/
├── models/          Player, Roll, GameState
├── dice/            DiceType, DiceFormula, DiceRoller, DiceResult
├── physics/         Vector3, Quaternion, PhysicsBody, PhysicsWorld
├── rendering/       DiceShaders, DiceMeshFactory, DiceRenderer, DiceGLView
├── network/
│   ├── messages/    NetworkMessage (sealed class, all message types)
│   ├── LobbyServer  TCP server — runs on DM device
│   ├── LobbyClient  TCP client — runs on player devices
│   └── NetworkManager  IP address utilities
├── database/        AppDatabase, RollEntity, RollDao
├── repository/      RollRepository
└── ui/
    ├── theme/       Color, Type, Theme
    ├── navigation/  AppNavigation, Routes
    ├── viewmodels/  HomeViewModel, LobbyViewModel, DiceViewModel
    ├── screens/     HomeScreen, CreateLobbyScreen, JoinLobbyScreen,
    │                LobbyScreen, DiceScreen
    └── components/  DiceAnimationOverlay, RollLogList, PlayerList, DiceSelector
```

## Network Protocol

All messages are newline-delimited JSON with a `"type"` discriminator field.

| Direction | Message | Purpose |
|---|---|---|
| Client → Host | `JoinRequest` | Connect + send player info |
| Client → Host | `RollRequest` | Ask host to roll on behalf of player |
| Client → Host | `LeaveRequest` | Graceful disconnect |
| Host → Client | `JoinAck` | Full lobby state on join |
| Host → All | `PlayerConnected` | Broadcast new player |
| Host → All | `PlayerDisconnected` | Broadcast departure |
| Host → All | `RollResult` | Authoritative dice result |
| Host → Non-DM | `HiddenRollNotice` | DM rolled — result hidden |
| Host → All | `RevealRoll` | DM reveals hidden roll |

**Roll authority is always host-side** — clients send a formula request, the host computes and broadcasts the result, preventing cheating.

## Physics Engine

`PhysicsWorld` runs a semi-implicit Euler integration step at ~60 Hz:

1. Apply gravity (`−9.81 m/s²`) to each body's velocity
2. Integrate position from velocity
3. Integrate orientation from angular velocity (via quaternion multiplication)
4. Resolve ground-plane collisions with configurable restitution (bounciness) and friction
5. Resolve wall collisions inside a 4 m × 4 m arena
6. Detect when a body has been at rest for 0.6 s → mark as `settled`
7. On settle: find the face whose local normal is most aligned with world +Y → that face's value is the die result

Face normals for all 7 die types are pre-computed in `PhysicsWorld.buildFaceNormals()`.

## Building & Running

### Prerequisites

- Android Studio Hedgehog (2023.1) or newer
- JDK 17
- Android SDK API 26+

### Steps

1. Clone the repository
2. Open in Android Studio
3. Let Gradle sync resolve all dependencies
4. Run on a physical device or emulator (API 26+)
   - Physical devices recommended for OpenGL ES performance

### Connecting devices

1. The DM opens the app → **Create Game** → **Start Lobby Server**
2. The DM shares the displayed IP address (e.g. `192.168.1.42`) with players
3. Players open the app → **Join Game** → enter the IP → **Connect**
4. All devices must be on the same WiFi network
5. Port **9876** must not be blocked by any firewall

## Running Unit Tests

```bash
./gradlew test
```

Tests cover:
- `DiceFormulaTest` — formula parsing and roller output ranges
- `PhysicsTest` — vector maths, quaternion operations, physics settling

## Optional Enhancements (not yet implemented)

- mDNS / UDP broadcast for automatic lobby discovery
- Sound effects for dice rolls
- Dice 3D model OBJ import for higher-fidelity meshes
- Spectator mode (observe without rolling)
- Campaign history export
