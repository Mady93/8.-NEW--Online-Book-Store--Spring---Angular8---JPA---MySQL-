const express = require('express');
const jwt = require('jsonwebtoken');
const bodyParser = require('body-parser');
const routes = require('./controller.js');
const { createProxyMiddleware, fixRequestBody } = require('http-proxy-middleware');
const { privateKey, publicKey } = require('./keys.js');
const fetch = require('node-fetch');


const refreshTokenTime = 1200;
const app = express();
app.use(express.json());

const proxyConfig = {
    target: 'http://localhost:8080',
    changeOrigin: true,
    onProxyReq: fixRequestBody
};

const proxyMiddleware = createProxyMiddleware('/', proxyConfig);

// Middleware per il parsing del corpo delle richieste
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));


//Enable CORS for all HTTP methods
app.use(function (req, res, next) {

    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Methods", "GET, PUT, PATCH, POST, DELETE, OPTIONS");
    res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
    res.header("Access-Control-Expose-Headers", "Authorization");
    next();
});

app.use(function (req, res, next) {
    console.log('Request:', req.method, req.url);
    console.log('Headers:', req.headers);
    console.log('Body:', req.body);
    next();
});


/*
 questo codice crea un server Node.js che agisce come proxy per le richieste verso un backend Spring, controllando e rinnovando i token JWT e gestendo l'autenticazione degli utenti.
  */


sendJWT((status) => {
    let x = (status == 200);
    let msg = "invio PubKey: " + (x ? "OK" : "ERR");
    console.log(msg);

    if (!x) process.exit();

});




let tokenMap = {};


const accessTable = [
    //Rotte users
    { path: /^\/users\/get/, groups: ["Admin"] },
    { path: /^\/users\/\d+\/one$/, groups: ["Admin"] },
    { path: /^\/users\/\d+\/delete$/, groups: ["Admin"] },
    { path: /^\/users\/deleteAll$/, groups: ["Admin"] },
    { path: /^\/users\/setRole$/, groups: ["Admin"] },
    { path: /^\/users\/deleteAll$/, groups: ["Admin"] },


    // Rotte orders
    { path: /^\/orders\/get(\?.*)?$/, groups: ["Admin", "User", "Seller"] },
    { path: /^\/orders\/\d+\/one$/, groups: ["Admin", "User", "Seller"] },
    { path: /^\/orders\/\d+\/delete$/, groups: ["Admin"] },
    { path: /^\/orders\/\d+\/books$/, groups: ["Admin", "User", "Seller"] },
    { path: /^\/orders\/\d+\/count$/, groups: ["Admin", "User", "Seller"] },
    { path: /^\/orders\/users\/\d+\/count$/, groups: ["Admin", "User", "Seller"] },
    { path: /^\/orders\/users\/\d+\/delete$/, groups: ["Admin"] },
    { path: /^\/orders\/users\/\d+\/deleteAll$/, groups: ["Admin"] },
   

    // Rotte JoinTables
    { path: /^\/joinTables\/add$/, groups: ["Admin", "User", "Seller"] },
    { path: /^\/joinTables\/\d+\/get$/, groups: ["Admin", "User", "Seller"] },
    { path: /^\/joinTables\/update\/\d+\/\d+$/, groups: ["Admin"] },
    { path: /^\/joinTables\/\d+\/delete$/, groups: ["Admin"] },
    { path: /^\/joinTables\/deleteAll$/, groups: ["Admin"] },

    // Rotte Books
    { path: /^\/books\/upload$/, groups: ["Admin"] },
    { path: /^\/books\/add$/, groups: ["Admin"] },
    { path: /^\/books\/\d+\/one$/, groups: ["Admin"] },
    { path: /^\/books\/update\/\d+$/, groups: ["Admin", "Seller"] },
    { path: /^\/books\/\d+\/delete$/, groups: ["Admin"] },
    { path: /^\/books\/deleteAll$/, groups: ["Admin"] },

];





function getPermissionByPath(path) {

    for (let ele of accessTable) {
        let x = path.match(ele.path);
        if (x != null) return ele.groups;
    }

    return null;

}





function sendJWT(cb) {
    const url = "http://127.0.0.1:8080/users/setPubKey";
    const options = {
        method: 'POST',
        headers: {
            'Content-Type': 'text/plain',
        },
        body: publicKey,
    };

    fetch(url, options)
        .then(x => {
            cb(x.status);
        });
}


function isMasterTokenValid(uid, cb) {

    const url = "http://127.0.0.1:8080/users/" + uid + "/getTokenTime";
    const options = {
        method: 'GET',
        headers: {}
    };

    fetch(url, options)
        .then(res => {
            res.json().then(data => {
                cb(res.status, data.time);
            })
        })

}


function checkAndRenewToken(uid, exp, role) {
    const now = Math.floor(Date.now() / 1000);
    if (exp && now > exp) {
        return new Promise((resolve, reject) => {

            isMasterTokenValid(uid, (status, time) => {

                let key = "" + uid;

                if (tokenMap[key] === undefined || now > tokenMap[key]) {
                    tokenMap[key] = (now + refreshTokenTime);
                } else {
                    resolve({ state: 0 });
                }


                if (status == 200) {
                    let t = (time > refreshTokenTime) ? refreshTokenTime : parseInt(time);

                    t = t + "s";

                    const refreshToken = jwt.sign({
                        role: role
                    },
                        privateKey,
                        {
                            algorithm: 'RS256',
                            expiresIn: t,
                            subject: uid
                        });

                    resolve({ state: 1, token: refreshToken });
                } else {
                    resolve({ state: 2 });
                }

            });

        });
    } else {
        return Promise.resolve({ state: 2 });
    }
}





// Middleware per verificare il token JWT
async function checkToken(req, res, next) {

    //debugger;
    let path = req.originalUrl;
    let authUsers = getPermissionByPath(path);

    console.log('Auth Users:', getPermissionByPath(path));

    // Gestisce le richieste OPTIONS separatamente e consente loro di passare senza verificare il token
    if (req.method === "OPTIONS" || path.endsWith("/register")) {
        next();
        return;
    }

    let token = req.headers.authorization;
    let role = "*";
    let exp = 0;

    if (token != null && token.indexOf("Bearer") == 0) {
        token = token.substr(7);
        try {
            const decoded = await jwt.verify(token, publicKey, { algorithms: ['RS256'], ignoreExpiration: true });
            role = decoded.role;
            uid = decoded.sub;
            exp = decoded.exp;

            /* se il token e' scaduto e il token master e' ancora valido effettuto un refresh */
            checkAndRenewToken(uid, exp, role).then(res0 => {
                switch (res0.state) {
                    case 0:
                        res.status(406).json({ message: "expired token invalidated, use the new token" });
                        break;
                    case 1:
                        res.setHeader('Authorization', res0.token);
                    case 2:
                        if (authUsers == null || authUsers.includes(role)) {
                            // Puoi andare avanti
                            next();
                        } else {
                            // Rispondi con status 403 Forbidden
                            const timestamp = Date.now();
                            const status = role === "*" ? 401 : 403;
                            res.status(status).json({ message: "Forbidden", timestamp, status });
                        }
                }
            });
        } catch (err) {
            return res.status(401).json(err);
        }
    } else {
        /* non ho un token ma provo comunque a prendere la risorsa */
        if (authUsers == null || authUsers.includes(role)) {
            // Puoi andare avanti
            next();
        } else {
            // Rispondi con status 403 Forbidden
            const timestamp = Date.now();
            res.status(401).json({ message: "Unauthorized", timestamp: timestamp, status: 401 });
        }
    }


}





// Endpoint per il login
app.options('/login', (req, res) => {
    // Aggiungi le intestazioni CORS necessarie e invia una risposta vuota
    res.header("Access-Control-Allow-Methods", "POST");
    res.send();
});
app.post('/login', routes.loginRoute);
// endpoint proxy su spring
app.use('/', checkToken, proxyMiddleware);
// Avvia il server
const port = 3000;
app.listen(port, () => {
    console.log(`Server avviato su http://localhost:${port}`);
});



