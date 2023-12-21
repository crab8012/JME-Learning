# JME-Learning
This repository contains a single JME3 project using Maven that I will continuously update as I learn more about how to use the game engine. This might turn into a fully-fledged game, or it might just be a sandbox for testing random mechanics or techniques.


ToDo:

My next few commits should encompass multiple things...
1) The "Main Menu" should replace the start button with buttons for each test AppState[DONE]
2) The "Main Menu" should be callable with "ESC"[DONE]
3) The application should no longer close when "ESC" is pressed[DONE]
4) Going to the "Main Menu" using "ESC" should disable and detach the current test AppState[DONE]

And then:
5) There should be a new "Maze" AppState[DONE]
6) The "Maze" should have a maze model[DONE]
7) The "Maze" should have a UVMapped texture[DONE]
8) The "Maze" should have a camera with gravity and collision detection[DONE]
9) The player should start within the "Maze"[DONE]

After that:
10) There should be a new "Island" AppState [DONE]
11) The Island should have a small-ish island model [REPLACE WITH #19]
12) The Island model should have a sand texture [DONE]
13) The Island should have a tree or plant model on it (separate model) [DONE]
14) The Island should have a dock model on the edge (separate model) [DONE]
15) The Island should have sounds that play when the player walks on the sand [DONE]
16) The Island sound should change when the player walks on the bridge [DONE]
17) The Island should be surrounded by a SeaMonkey WaterFilter (aka a big ocean)

Then:
18) There should be a different "Island" AppState [MERGED WITH FIRST ISLAND APPSTATE]
19) Instead of using an island model, it should use JMonkeyEngine's Terrain features [DONE]
20) The Island V2 should have an animated creature somewhere

And:
21) There should be a cave AppState that the player can walk around in
22) It should have ambient lighting from a campfire in the middle
23) There should be smoke particle effects coming off the fire
24) There should be positional audio with fire noise
25) There should be positional audio of rain from the mouth of the cave
26) The audio scene/fx should change as the player leaves the cave
27) There should be rain particles from outside
28) There should be occasional water drip particles and noises from the roof of the cave
29) There should be at least one torch on the wall with ambient lighting making the cave glow a little
30) There should be a small table with a ball on it
31) The player should be able to push the ball off the table
32) The ball should act "realistically" as it falls off the table
33) The ball should respawn a few seconds after it hits the floor

-----
34) The Main Menu should have a checkbox or an alternate button for the Island and Cave scenes
35) This alternate button should add a HUD
36) For the Cave scene there should be a button to play a flythrough cutscene

37) There needs to be a field AppState
38) The field AppState should just contain a small terrain area with flowing grass
39) The field AppState should also include some basic sound effects

40) There needs to be a Farm AppState that extends the Field
41) It should have a barn and an outdoor pen
42) There should be some animated farm animals
43) The farm animals should have simple AI to walk around aimlessly

43.5) There needs to be a player class and an item/weapon/tool class

44) There needs to be a FirstPersonShooter AppState that extends the Field
45) It should have a simple gun with an ammo count on the HUD
46) The gun should stick with the player as they move, like a normal FPS
47) The player should be able to aim down sights with the gun or hipfire with it
48) There should be a shooting range with different targets.
49) The targets should record how many times it was hit in different key spots
50) The targets should display their hits as a number nearby
51) There should be a variety of shapes, sizes, and distances of the targets

52) There should be a Collision and Interaction AppState
53) There should be a number of doors, buttons, levers, consoles, and other things
54) This will test a number of ways of interacting with the world
55) It must have raycasting, collision trigger boxes, etc.
56) Some interactions should have an effect elsewhere in the level
57) Some interactions should pull up a temporary GUI
58) Some interactions should impact the player's hud
59) Some interactions should add an objective marker in the world
59.5) An interaction should provide a notification pop-up to the player

60) There should be an Armory AppState
61) There should be a number of tables and shelves with items
62) The player should be able to pick up some items
63) The player should be able to equip some items
64) The player should be able to scroll through their equipped items
65) The player should be able to interact with other items

67) There should be a Ragdoll and Death AppState
68) There should be several 3d models of creatures and items
69) The player should be able to shoot the creatures and items
70) When hit, the creatures and items should produce appropriate particles
71) When hit, the creatures should ragdoll
72) After a certain amount of time, the creatures that have been ragdolled should reset.

73) There should be a Outdoor City and Race Track AppState
74) The player should be able to occupy a few different types of vehicle and drive them around the level
75) The scene should have a day/night cycle, or otherwise some sort of dynamic skybox and lighting changes
76) The vehicles should have functional headlights
77) At least one vehicle should have additional lighting, such as underglow

78) There should be a Map Streaming AppState
79) It should start small, surrounding the player with buildings that they can walk through
80) As the player moves, buildings and terrain within a radius of the player should load and unload
81) The player should not be able to tell that things are loading as they walk, except by app slowdown (bad)
82) The player should be able to enter some buildings that are "bigger on the inside" (aka entirely new levels)

83) The project should be cleaned up and split off into a smaller project with one or two of the advanced scenes
84) This splinter project should focus on networking and multiplayer (co-op at least)

85) Using the information learned through this project, a story-mode game should be designed
86) The story-mode game should be developed.
87) The story-mode should last at least 4 hours of gameplay
88) The story-mode game should have a similar, separate multiplayer mode (more competitive than not)
89) The story-mode game should have a similar, yet different story mode for co-op.
89) This could be either a concurrent story, a prequel, or a sequel story
90) The co-op mode should last at least 4 hours of gameplay
