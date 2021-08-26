from sqlalchemy import create_engine, Column, Integer, String, ForeignKey, DateTime
from sqlalchemy.orm import declarative_base, relationship, sessionmaker

from flask import current_app, g, jsonify
from flask.cli import with_appcontext

from datetime import datetime

import json
import click

Base = declarative_base()


class User(Base):
    __tablename__ = 'user_account'

    id = Column(Integer, primary_key=True)
    username = Column(String(32), nullable=False, unique=True)
    password = Column(String(128), nullable=False)
    created = Column(DateTime, nullable=False, default=datetime.now())

    collections = relationship(
        'Collection', back_populates='user', lazy="joined")

    def __repr__(self):
        return jsonify({
            "id": self.id,
            "username": self.username,
            "password": self.password,
            "created": self.created,
            "collection": jsonify([collection.__repr__() for collection in self.collections])
        })


class Collection(Base):
    __tablename__ = 'user_collection'

    id = Column(Integer, primary_key=True)
    title = Column(String(32), nullable=False)
    description = Column(String(128))
    schema = Column(String(512))
    created = Column(DateTime, nullable=False, default=datetime.now())

    user_id = Column(Integer, ForeignKey("user_account.id"))
    user = relationship("User", back_populates="collections")

    entries = relationship('Entry', back_populates='collection')

    def __repr__(self):
        return jsonify({
            "id": self.id,
            "title": self.title,
            "description": self.description,
            "schema": self.schema,
            "created": self.created,
            "user_id": self.user_id
        })


class Entry(Base):
    __tablename__ = 'collection_entry'

    id = Column(Integer, primary_key=True)
    data = Column(String(512), nullable=False)
    created = Column(DateTime, nullable=False, default=datetime.now())

    collection_id = Column(Integer, ForeignKey("user_collection.id"))
    collection = relationship("Collection", back_populates="entries")

    def __repr__(self):
        return jsonify({
            "id": self.id,
            "data": self.data,
            "created": self.created,
            "collection_id": self.collection_id
        })

# The object used to connect to the database, ensures all data is commited and sessions closed


class SQLConnection(object):
    def __init__(self, connection_string):
        self.connection_string = connection_string
        self.session = None

    def __enter__(self):
        engine = create_engine(self.connection_string, echo=True, future=True)
        Session = sessionmaker(expire_on_commit=False)
        self.session = Session(bind=engine)
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        self.session.commit()
        self.session.close()

# get_db returns the database object, creates one if it does not exist


def get_db():
    if 'db' not in g:
        # connects to db and creates row factory
        g.db = SQLConnection(current_app.config['DATABASE'])

    return g.db

# Tears down an existing database and creates the tables with the given schema


def init_db():
    engine = create_engine(
        current_app.config['DATABASE'], echo=True, future=True)
    Base.metadata.drop_all(engine)
    Base.metadata.create_all(engine)

# init_db_command creates the command that creates the tables in the database


@click.command('init-db')
@with_appcontext
def init_db_command():
    init_db()
    click.echo("Initialized database")

# Passed into the application to add the cli command


def init_app(app):
    app.cli.add_command(init_db_command)
