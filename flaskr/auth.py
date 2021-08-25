import functools

from flask import Blueprint, flash, g, redirect, render_template, request, session, url_for
from werkzeug.security import check_password_hash, generate_password_hash

from flaskr.db import get_db, User
from sqlalchemy.exc import IntegrityError

bp = Blueprint('auth', __name__, url_prefix='/auth')

@bp.route('/register', methods=('GET', 'POST'))
def register():
    if request.method == 'POST':
        # Get data from form
        username = request.form['username']
        password = request.form['password']

        # Make sure data exists
        if not username or not password:
            flash('Username and Password required')
            return render_template('auth/register.html')

        # Create the new user and add it to the database
        new_user = User(username=username, password=generate_password_hash(password))
        try:
            with get_db() as db:
                db.session.add(new_user)
        except IntegrityError:
            flash("User already exists")
            return render_template('auth/register.html')

        return redirect(url_for('auth.login'))

    return render_template('auth/register.html')

@bp.route('/login', methods=('GET', 'POST'))
def login():
    if request.method == 'POST':
        # Gets the data from the form
        username = request.form['username']
        password = request.form['password']

        if not username or not password:
            flash("Missing username or password")
            return render_template('auth/login.html')

        # Gets the users information from the database
        with get_db() as db:
            user = db.session.query(User).filter_by(username = username).first()
            # Checks if user exists and that the password matches up
            if not user or not check_password_hash(user.password, password): 
                flash('Incorrect username or password')
                return render_template('auth/login.html')

            return redirect(url_for('index'))


    return render_template('auth/login.html')

@bp.route('/logout')
def logout():
    # Clears the user from the current session
    session.clear()
    return redirect(url_for('index'))

@bp.before_app_request
def load_logged_in_user():
    # Checks if there is a user in the current session
    user_id = session.get('user_id')

    # Adds the user to the request if they are logged in
    if user_id is None:
        g.user = None
    else:
        with get_db() as db:
            db.session.query(User).get(user_id)

# When added to a route it ensures that the user is currently logged in
def login_required(view):
    @functools.wraps(view)
    def wrapped_view(**kwargs):
        if g.user is None:
            return redirect(url_for('auth.login'))
        
        return view(**kwargs)
    
    return wrapped_view