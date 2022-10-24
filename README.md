<p align="center">
<img src="img/logo.png"><br>
<h1 align="center">Copilot</h1>
</p>

Shows the next thing you should click and alerts you about it.
Every single step is highlighted, so you can mindlessly click whilst doing something else.

## Demo
https://user-images.githubusercontent.com/115373370/195444530-a0a17efc-7d8c-4574-b306-4a7f7c64e578.mp4

## Running
As long this plugin is not in the plugin hub:
* Open the project in Intellij.
* Run the CopilotPluginTest in the top right corner of the screen.

## Supported activities
### Woodcutting
General purpose for chopping trees and banking the logs.  
* Select a tree and log type by clicking it.

### Crafting
General purpose for bankstanding skills.  
* Enter your tool. Examples: knife, glassblowing pipe
* Enter your resource. Examples: yew logs, molten glass
* Enter your product. Examples: yew longbow(u), lantern lens

### Fishing & Cooking
Fishing trout and salmon at Barbarian Village, and cooking it.

### General inactivity alert
Use this if the activity you want to do is not defined above, or if you only want the plugin to alert you when you are inactive.  
Alerts you when you have not yet animated, moved or clicked something for set time.  
It does not show you what to click.

## Features
Configuration options:  
![Settings configuration](/img/settings.png "Copilot configuration options.")  
Some configuration options further explained:
* Alert delay: after how many milliseconds of inactivity you are alerted.
* Alert sound ID: the sound played when you are alerted. 
You can choose from the [Sound IDs wiki page](https://oldschool.runescape.wiki/w/List_of_in-game_sound_IDs).
* Alt alert sound ID: the sound played when you are alerted and your inventory is full.
* Amount of alerts: the amount of alerts that sound when you are inactive.
You could for example set a single very loud alert, or 1000 soft ticks to keep reminding you to interact with the game.

## Tips
* Use the menu entry swapper plugin to make every action left-clickable.
* Use the camera plugin to zoom out more and enable vertical camera.
* Increase the render distance with the GPU plugin.
* Enable Esc to close the current interface.

## Planned features
* Menu like quest helper
* Deposit boxes
* Tempoross
* Giant's Foundry
* Power skilling option
* Base dim for screen
* Loot alerter
* Redwood support

## Denied features
* Hide widgets when entity interaction is needed. Denied, because it breaks a lot of scripts. You can't for example check if the bank is open, because the bank widget will be hidden.

## Bugs
* None known


## Credits
Plugin developed by [Jort](https://jort.dev).  
Plugin testers:
* [Debug1010](https://linktr.ee/01)  

Plugin inspired by the following plugins:
* [Quest Helper](https://github.com/Zoinkwiz/quest-helper)
* [Skilling Notifications](https://github.com/jodelahithit/runelite-plugins/tree/skilling-notifications)
* [Easy Blast Furnace](https://github.com/Toofifty/easy-blastfurnace)
* [Easy Giants Foundry](https://github.com/Toofifty/easy-giantsfoundry)
