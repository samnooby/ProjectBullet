import functools
from http import HTTPStatus

from flask import Blueprint, flash, g, redirect, jsonify, request, session, make_response
from werkzeug.security import check_password_hash, generate_password_hash

from sqlalchemy.orm import joinedload
from sqlalchemy.exc import IntegrityError

from flaskr.db import get_db, User


bp = Blueprint('auth', __name__, url_prefix='/auth')

@bp.route('/register', methods=(['POST']))
def register():
    if request.method == 'POST':
        # Get data from form
        username = request.form['username']
        password = request.form['password']

        # Make sure data exists
        if not username or not password:
            return make_response("Username and password required", HTTPStatus.BAD_REQUEST)

        # Create the new user and add it to the database
        new_user = User(username=username, password=generate_password_hash(password))
        try:
            with get_db() as db:
                db.session.add(new_user)
        except IntegrityError:
            return make_response("User already exists", HTTPStatus.CONFLICT)

        return make_response(new_user.__repr__(), HTTPStatus.CREATED)

@bp.route('/login', methods=(['POST']))
def login():
    if request.method == 'POST':
        # Gets the data from the form
        username = request.form['username']
        password = request.form['password']

        if not username or not password:
            return make_response("Missing username or password", HTTPStatus.BAD_REQUEST)

        # Gets the users information from the database
        with get_db() as db:
            user = db.session.query(User).filter_by(username = username).options(joinedload(User.collections)).first()
            # Checks if user exists and that the password matches up
            if not user or not check_password_hash(user.password, password): 
                return make_response('Incorrect username or password', HTTPStatus.BAD_REQUEST)

            session.clear()
            session['user_id'] = user.id


            return make_response(user.__repr__(), HTTPStatus.OK)

@bp.route('/logout')
def logout():
    # Clears the user from the current session
    session.clear()
    return make_response("Logged out", HTTPStatus.OK)

@bp.before_app_request
def load_logged_in_user():
    # Checks if there is a user in the current session
    user_id = session.get('user_id')
    print(f'Got { user_id } from session')

    # Adds the user to the request if they are logged in
    if user_id is None:
        g.user = None
    else:
        with get_db() as db:
            user = db.session.query(User).get(user_id)
        g.user = user

# When added to a route it ensures that the user is currently logged in
def login_required(view):
    @functools.wraps(view)
    def wrapped_view(**kwargs):
        if g.user is None:
            return redirect(url_for('auth.login'))
        
        return view(**kwargs)
    
    return wrapped_view