from flask import Blueprint, flash, g, render_template

from flaskr.auth import login_required
from flaskr.db import get_db, Collection

bp = Blueprint('collection', __name__, url_prefix='/collection')

@bp.route('/index')
@login_required
def index():
    # with get_db() as db:
    #     new_collection = Collection(title="test", description="tester", schema="schema", user_id=1)
    #     db.session.add(new_collection)
    return render_template('collection/index.html', collections=g.user.collections)