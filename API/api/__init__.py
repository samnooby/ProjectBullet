from flask import Flask

import os


def createApp(test_config=None):
    #Create the app with the given configs
    app = Flask(__name__, instance_relative_config=True)
    #TODO: Add database to config
    app.config.from_mapping(
        'SECRET_KEY'='dev'
    )

    #If we are not testing use the configs from the file
    if test_config is None:
        app.config.from_file('config.py', silent=True)
    else:
        app.config.from_mapping(test_config)
    
    #Ensures that the folder for the instance exists
    try:
        os.mkdir(app.instance_path)
    except OSError:
        pass

    #TODO: Add app routes here

    return app