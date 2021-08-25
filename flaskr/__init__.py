import os

from flask import Flask

def create_app(test_config=None):
    # Create the app with given config
    app = Flask(__name__, instance_relative_config=True)
    app.config.from_mapping(
        SECRET_KEY='dev',
        DATABASE="mysql+pymysql://sam:tester@localhost/bullet_dev?charset=utf8mb4"
    )

    if test_config is None:
        # Load config if it exists when not testing
        app.config.from_pyfile('config.py', silent=True)
    else:
        # Load the test config
        app.config.from_mapping(test_config)

    # Ensures the instance folder exists
    try:
        os.makedirs(app.instance_path)
    except OSError:
        pass

    #TODO: Set up app routes
    from . import auth
    app.register_blueprint(auth.bp)

    from . import db
    db.init_app(app)

    return app