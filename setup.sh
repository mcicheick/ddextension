#!/usr/bin/env bash
error="false"
if [ "X$PLAY_HOME" = "X" ]
then
	echo "Please define PLAY_HOME environnement variable."
	error="true"
fi
if [ "X$DD_EXTENSION_HOME" = "X" ]
then
	echo "Please define DD_EXTENSION_HOME environnement variable."
	error="true"
fi
if [ error = "true" ]
then
	echo "Please defined variables."
	exit 1
fi
echo PLAY_HOME=$PLAY_HOME
echo DD_EXTENSION_HOME=$DD_EXTENSION_HOME
exit 0

