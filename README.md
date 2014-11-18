# Module Djamma Dev Extension
# Step 1 get ddextension
git clone https://github.com/mcicheick/ddextension.git ddextension

# Step 2 Defined environnement variable
export PLAY_HOME=/path/to/play_home

export DD_EXTENSION_HOME=/path/to/ddextension

# Step 3 replace script play bye custom
cp $DD_EXTENSION_HOME/play $PLAY_HOME/play

# Enjoy !

