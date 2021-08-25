from sqlalchemy import create_engine, Column, Integer, String, ForeignKey
from sqlalchemy.orm import declarative_base, relationship, sessionmaker

import json
import click
from flask import current_app, g
from flask.cli import with_appcontext

Base = declarative_base()

class SQLConnection(object):
    def __init__(self, connection_string):
        self.connection_string = connection_string
        self.session = None
    
    def __enter__(self):
        engine = create_engine(self.connection_string, echo=True, future=True)
        Session = sessionmaker()
        self.session = Session(bind=engine)
        return self
    
    def __exit__(self, exc_type, exc_val, exc_tb):
        self.session.close()

class User(Base):
    __tablename__ = 'user_account'

    id = Column(Integer, primary_key=True)
    username = Column(String(32), nullable=False, unique=True)
    password = Column(String(128), nullable=False)

    collections = relationship('Collection', back_populates='user')

class Collection(Base):
    __tablename__ = 'user_collection'

    id = Column(Integer, primary_key=True)
    title = Column(String(32), nullable=False)
    description = Column(String(128))
    schema = Column(String)

    user = relationship("User", back_populates="collections")

    entries = relationship('Entry', back_populates='collection')

class Entry(Base):
    __tablename__ = 'collection_entry'

    id = Column(Integer, primary_key=True)
    data = Column(String, nullable=False)

    collection = relationship('Collection', back_populates="user")

# get_db returns the database object, creates one if it does not exist
def get_db():
    if 'db' not in g:
        # connects to db and creates row factory
        g.db = SQLConnection(current_app.config['DATABASE'])
    
    return g.db

# close_db removes the database object from the global and closes it if exists
def close_db(e=None):
    db = g.pop('db', None)

# Creates the tables with the given schema
def init_db():
    Base.metadata.create_all(engine)

# init_db_command creates the command that creates the tables in the database
@click.command('init-db')
@with_appcontext
def init_db_command():
    init_db()
    click.echo("Initialized database")

def init_app(app):
    app.teardown_appcontext(close_db)
    app.cli.add_command(init_db_command)

