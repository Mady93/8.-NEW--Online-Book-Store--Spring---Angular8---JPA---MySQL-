const jwt = require('jsonwebtoken');
const http = require('http');
const fetch = require('node-fetch');
const { privateKey } = require('./keys.js');
const constants = require('./define.js');



/*
questo codice si occupa di autenticare un utente attraverso una richiesta di login al backend Spring. 
Se l'autenticazione ha successo, genera due token JWT: uno per l'accesso iniziale (scadenza di 2 ore) e un altro per il rinnovo del token (scadenza di 20 minuti). 
Invia il token di rinnovo al frontend e invia il token di accesso al backend Spring
*/

function sendTokenToSpring(uid, token) {

    const url = "http://127.0.0.1:8080/users/saveToken";
    const options = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            'uid': uid,
            'token': token
        })
    };

    return fetch(url, options);

}

exports.loginRoute = (req, res) => {

    const url = "http://127.0.0.1:8080/users/login";
    const options = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(req.body),
    };

    fetch(url, options)
        .then(x => {

            if (x.status == 200) {

                x.json().then(data => {


                    const token = jwt.sign({
                        role: data.role
                    },
                        privateKey,
                        {
                            algorithm: 'RS256',
                            expiresIn: '2h',
                            subject: data.id
                        });

                    sendTokenToSpring(data.id, token).then(x => {

                        if (x.status == 200) {

                            const tokenRefresh = jwt.sign({
                                role: data.role
                            },
                                privateKey,
                                {
                                    algorithm: 'RS256',
                                    expiresIn: constants.REFRESH_TOKEN_TIME+'s',
                                    subject: data.id
                                });

                            res.setHeader('Authorization', tokenRefresh);
                            res.status(200).send(data);


                        } else {
                            res.status(x.status).send(data);
                        }

                    });

                });

            } else {
                x.text().then(data => {
                    res.status(x.status).send(data);
                });
            }
        });
};







