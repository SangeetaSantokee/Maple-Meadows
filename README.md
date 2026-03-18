# Maple-Meadows
2D Java Swing game where you control a bat collecting foxes across a scrolling village map, featuring pixel-level image effects and context-sensitive audio.

🦇 Player & Fox Village
A 2D Java Swing game where you control a player navigating a scrolling village world, hunting down foxes.

🎮 Gameplay
Guide your player through a village map, tracking down and collecting 6 hidden foxes. Watch your surroundings. Foxes move on their own and will appear when you least expect it. Collect all 6 to win.

⚠️ Hazards
- River: Stray into the river and your player begins to fade and disappear
- : When a fox enters your view, the world shifts to an eerie orange tint and the music changes to signal danger
- Game Over: Once all foxes are collected, the screen drains to grayscale and the world freezes

✨ Visual Effects
- Orange tint
- Disappear
- GrayScale 
All effects are implemented via direct pixel manipulation on a double-buffered BufferedImage, using bitwise operations to extract and modify ARGB colour channels.

🛠️ Built With
- Java Swing (JPanel, BufferedImage, Graphics2D)
- Double buffering for smooth rendering
- Camera/viewport scrolling system for a world larger than the screen
- Pixel-level image manipulation (grayscale, alpha transparency, colour tinting)
- Clip-based sound management

🕹️ Controls
- Moving: arrow keyr (← → ↑ ↓)
- Shooting: space button
