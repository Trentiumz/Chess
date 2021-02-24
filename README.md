# Chess
This is my attempt at creating a chess ai

# Overview
The game is seperated into 3 main folders;

kuusito/tinysound: is an api folder for playing sound that I just didn't bother to compress, so now it's there

engine: is the main folder for the game mechanics - it is also the starting point for the program

bot: is the folder for the bot, and the engine will call the objects in bot in order to get the move

data: is the folder for data that the bot and game will use

# Engine
The engine is split into many classes; the program will start in the main file

A state pattern is utilized for seamless switching between the different "screens". There's the Main State, Start State and End State for the Actual Chess game, the start page and the end page. 
The different states are handled in the "Main" class and each state also will switch to the next

The Tools class is a utility class and provides many conversion functions but also stores sprites and file data. You'll need to initialize it in order, as the Tools Class also 
handles drawing images and the like. 

The BoardClient class handles requests from the user, with the Main State class acting as a controller from the ui to the BoardClient. The BoardClient also interacts with the Board, 
which stores the current position of the current game but also provides many utility functions for getting information about the board. 

We also have an abstract Pieces class, which represents the pieces. They have an instance of the board class, provide utility functions for information pertaining to the piece, but 
also handles user and bot actions such as moving the piece. Each board has many pieces, which is stored in an ArrayList called Pieces

Finally, to handle undoing moves, we have a move class, which gives an instruction and how we're supposed to "undo" a move. This is useful in a practical scenario, but also very 
important if we want our bot to be able to "simulate" moves without recopying the board each time. 

# Bot
The bot is split into 2 main classes; BotMain and Evaluator

BotMain is the main bot, and the MainState class will have an instance of BotMain and will request moves from BotMain. This class, in getting its move will utilize the Evaluator class. 

The Evaluator class essentially runs the brunt of the minimax algorithm. It will look x moves into the future, playing out every possibility and return the highest rating that the side can get. 
Using this information of looking into the future, botMain will make its move. 

# Data
The Data is just a folder for the data the we will use. 

As soon as you open the folder, you'll see various .wav files; these are just music files to play when the program is running. 

Inside the sprites folder, you'll see various sprites that are used for rendering; the board and the pieces as well as the Start Screen Image is stored there. 

Inside the botAssist/positionRatings folder, you'll see various files that just store information on the rating change to apply for various position for various pieces. This 
will help the bot make decisions based not solely on piece value, but also on how valuable they are in a specific position. 

# Current Progress
Using a bit of java profiling, I was able to find that the main problem lied in the fact that we're recalculating possible moves each time we check for a check and each time we want the possible moves, I'm trying to find a way to recalculate possible moves in O(1) times, so that's what's happening right now. I also changed the pieces array to a grid, which vastly improves the speed of getting a specific piece. I've created a second branch for tackling these problems found in my JFR recordings.  