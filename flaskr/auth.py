import functools

from flask import Blueprint, flash, g, redirect, render_template, request, session, url_for
from werkzeug.security import check_password_hash, generate_password_hash

from flaskr.db import get_db, User

bp = Blueprint('auth', __name__, url_prefix='/auth')

@bp.route('/register', methods=('GET', 'POST'))
def register():
    if request.method == 'POST':
        # Get data from form
        username = request.form('username')
        password = request.form('password')

        # Make sure data exists
        if not username or not password:
            flash('Username and Password required')
            return render_template('auth/register.html')

        # Create the new user and add it to the database
        new_user = User(username=username, password=generate_password_hash(password))
        with get_db() as db:
            db.add(new_user)

    return render_template('auth/register.html')

@bp.route('/login', methods=('GET', 'POST'))
def login():
    return render_template('auth/login.html')
    