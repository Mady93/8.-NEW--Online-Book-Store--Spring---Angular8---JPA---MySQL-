


const jwt = require('jsonwebtoken');
const http = require('http');
const fetch = require('node-fetch');
const { privateKey } = require('./keys.js');










function sendTokenToSpring(uid, token){

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
                        
                        if (x.status == 200)
                        {

                            const token20 = jwt.sign({
                                role: data.role
                            },
                            privateKey,
                            {
                                algorithm: 'RS256',
                                expiresIn: '20s',
                                subject: data.id
                            });
                            
                            res.setHeader('Authorization', token20);
                            res.status(200).send("");

                        } else {
                            res.status(x.status).send("");
                        }

                    });



                    
                });

            } else {
                x.text().then(data => {
                    res.status(x.status).send({ msg: data });
                });
            }
        });
};






