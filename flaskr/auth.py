import functools

from flask import Blueprint, flash, g, redirect, render_template, request, session, url_for
from werkzeug.security import check_password_hash, generate_password_hash

from flaskr.db import get_db

bp = Blueprint('auth', __name__, url_prefix='/auth')

@bp.route('/register', methods=('GET', 'POST'))
def register():
    if request.method == 'POST':
        # Get username and password from form
        username = request.form('username')
        password = request.form('password')

        if not username or not password:
            flash('Username and Password required')
            return render_template('auth/register.html')


    return render_template('auth/register.html')