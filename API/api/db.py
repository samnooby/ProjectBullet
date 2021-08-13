import sqlite3
import click

from flask import current_app, g
from flask.cli import with_appcontext

# get_db returns the database object, if the current request is not connected it creates a new connection
def get_db():
    if 'db' not in g:
        g.db = sqlite3.connect(
            current_app.config['DATABASE'],
            detect_types=sqlite3.PARSE_DECLTYPES
        )
        g.db.row_factory = sqlite3.Row
    return g.db

# close_db removes the database from the request context and closes the connection
def close_db(e=None):
    db = g.pop('db', None)

    if db is not None:
        db.close()

# creates the database with the given schemas if it does not exist
def init_db():
    db = get_db()

    with current_app.open_instance_resource('schema.sql') as f:
        db.executescript(f.read().decode('utf8'))

# When you run the init-db click command it recreates the entire database
@click.command('init-db')
@with_appcontext
def init_db_command():
    init_db()
    click.echo('Initialized the database')

def init_app(app):
    app.teardown_appcontext(close_db)
    app.cli.add_command(init_db_command)