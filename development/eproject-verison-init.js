db.auth('xy', 'XY@dev1024');

db = db.getSiblingDB('eproject-version');

db.createUser({
    user: 'xy',
    pwd: 'XY@dev1024',
    roles: [
        {
            role: 'readWrite',
            db: 'eproject-version'
        }
    ]
});