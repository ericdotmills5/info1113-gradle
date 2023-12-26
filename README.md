# WizardTD: A Java Tower Defense Game

Welcome to WizardTD, a tower defense game I created to learn Java and Object-Oriented Programming for an [INFO1113](https://www.sydney.edu.au/units/INFO1113) assignment.

## Getting Started

To play WizardTD, follow these steps:

1. Download the `project` folder from this repository.
2. Run the `launch.jre` executable inside the `project` folder.

`game_screenshot.png` provides a taste of the game for those who wish not to download it.

## Source Code

The source code for the game is organized as follows:

- Game code: `project\src\main\java\WizardTD`
- Test cases: `project\src\test\java\WizardTD`

You can view the coverage of the test cases in the following file: `project\reports\jacoco\test\html\index.html`

Gameplay elements (starting mana, tower damage, map etc.) can be customised with `project\config.json`, allowing you to play your way!

You can create their own map text files to play in:
* X = Path (monsters spawn on map edges)
* S = Shrub (towers cannot be placed here)
* W = Wizard Hut (maps must have exactly 1)

Just remember to specify the name of the map within `project\config.json` under the _layout_ field.

## Gameplay

The aim of WizardTD is to survive all the waves of monsters by preventing them from reaching the wizard tower and draining all your mana. You can do this by placing and upgrading towers.

Controls are presented within the game. You can either click buttons on the right tab or press the respective keyboard key displayed on the button.

Enjoy the game and happy defending!
