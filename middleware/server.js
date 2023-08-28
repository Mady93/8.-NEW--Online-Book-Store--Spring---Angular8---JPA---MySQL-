const express = require('express');
const jwt = require('jsonwebtoken');
const bodyParser = require('body-parser');
const routes = require('./controller.js');
const { createProxyMiddleware, fixRequestBody } = require('http-proxy-middleware');
const { privateKey, publicKey } = require('./keys.js');
const fetch = require('node-fetch');
const constants = require('./define.js');
const url = require('url');


//const refreshTokenTime = 20;
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


// questo codice crea un server Node.js che agisce come proxy per le richieste verso un backend Spring, controllando e rinnovando i token JWT e gestendo l'autenticazione degli utenti.


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
    { path: /^\/users\/\d+\/one$/, groups: ["Admin", "User", "Seller", "Order"] },
    { path: /^\/users\/\d+\/delete$/, groups: ["Admin"] },
    { path: /^\/users\/deleteAll$/, groups: ["Admin"] },
    { path: /^\/users\/setRole$/, groups: ["Admin"] },
    { path: /^\/users\/deleteAll$/, groups: ["Admin"] },

    // Rotte orders
    { path: /^\/orders\/get(\?.*)?$/, groups: ["Admin", "User", "Seller", "Order"] },
    { path: /^\/orders\/\d+\/one$/, groups: ["Admin", "User", "Seller", "Order"] },
    { path: /^\/orders\/\d+\/delete$/, groups: ["Admin", "User", "Seller", "Order"] },
    { path: /^\/orders\/\d+\/books$/, groups: ["Admin", "User", "Seller", "Order"] },
    { path: /^\/orders\/\d+\/count$/, groups: ["Admin", "User", "Seller", "Order"] },
    { path: /^\/orders\/users\/\d+\/count$/, groups: ["Admin", "User", "Seller", "Order"] },
  
    // Rotte intersect table
    { path: /^\/order_book\/add$/, groups: ["Admin", "User", "Seller", "Order"] },
    { path: /^\/order_book\/\d+\/get$/, groups: ["Admin", "User", "Seller", "Order"] },
    { path: /^\/order_book\/update\/\d+\/\d+$/, groups: ["Admin", "User", "Seller", "Order"] },
   
    // Rotte Books
    { path: /^\/books\/upload$/, groups: ["Admin"] },
    { path: /^\/books\/add$/, groups: ["Admin"] },
    { path: /^\/books\/\d+\/one$/, groups: ["Admin", "Seller"] },
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
    console.log(exp-now);
    if (exp && now > exp) {
        return new Promise((resolve, reject) => {

            isMasterTokenValid(uid, (status, time) => {




                if (status == 200) {


                    if (time==0){

                        resolve({state: 3});

                    }else{


                        let key = "" + uid;

                        if (tokenMap[key] === undefined || now > tokenMap[key].exp) {
                            let exp = (now + constants.REFRESH_TOKEN_TIME);
                            tokenMap[key] = {exp: exp, tollerance: 3};
                        } else if (tokenMap[key].tollerance>0) {
                            tokenMap[key].tollerance--;
                            resolve({state: 2});
                        } else {
                            resolve({state: 0});
                        }


                        //genero un nuovo token per il front-end
                        let t = (time > constants.REFRESH_TOKEN_TIME) ? constants.REFRESH_TOKEN_TIME : parseInt(time);

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

                    }




                    

                } else {

                    resolve({ state: 2 });

                }
            });
        });

    } else {

        return Promise.resolve({ state: 2 });

    }
}


function handleToken(token)
{

    return new Promise((resolve, reject) => {

        let role = "*";
        let exp = 0;

        let response = {uid: null, role: "*", exp: 0, tokenRefreshResponse: null};

        if (token != null && token.indexOf("Bearer") == 0) {
            token = token.substr(7);

            try {

                jwt.verify(token, publicKey, { algorithms: ['RS256'], ignoreExpiration: true }, (err, decoded) => {

                    if (err) console.log(err);

                    role = decoded.role;
                    uid = decoded.sub;
                    exp = decoded.exp;

                    response.uid = uid;
                    response.role = role;
                    response.exp = exp;
            
                    // se il token e' scaduto e il token master e' ancora valido effettuto un refresh 
                    checkAndRenewToken(uid, exp, role).then(res0 => {
    
                        let trr = {status: 200, headers: {}, msg: ""};
    
                        switch (res0.state) {
                            case 0:
                                trr.status = 406;
                                trr.msg = "Expired token invalidated, use the new token";
                                //resolve(ret);
                                break;
                            case 1:
                                trr.Authorization = res0.token;
                            case 2:
                                break;
                            case 3:
                                trr.status = 401;
                                trr.msg = "Master token expired";
                        }

                        response.tokenRefreshResponse = trr;
                        resolve(response);


                    });
                });
        
            } catch (err) {
        
                //return res.status(401).json(err);
                //resolve({status: 401, msg: err});
                response.tokenRefreshResponse = {status: 401, msg: err};
                resolve(response);
        
            }
            
        }
        else
        {
            resolve(response);
        }

    });

}


function isUidCoherent(url, uid)
{
    let ret = true;
    let x = url.match(/orders\/(\d+)/);

    if (x && x[1]){
        let qpuid = x[1];
        ret = (qpuid==uid);
    }

    return ret;
}


function checkToken(req, res, next) {

    let path = req.originalUrl;
    let authUsers = getPermissionByPath(path);
    let token = req.headers.authorization;

    if (req.method === "OPTIONS" || path.endsWith("/register")) {
        next();
        return;
    }


    handleToken(token).then(res0 => {

        let response = {status: 200, headers: {}};

        //verifico se ho qualche risposta http da inoltrare
        let httpRes = res0.tokenRefreshResponse;
        if (httpRes)
        {
            if (httpRes.status != 200) res.status(httpRes.status).send(httpRes.msg);
            if (httpRes.Authorization) res.setHeader('Authorization', httpRes.Authorization);
        }


        //letto uid e role
        uid = res0.uid;
        role = res0.role;

        /* se ho un uid, estratto dal token e' sicuramente valido lo passo al backend per ulteriori controlli*/
        if (uid)
        {
            /*
            let parsedUrl = url.parse(path);
            let queryParams = querystring.parse(parsedUrl.query);
            queryParams.vuid = uid;
            queryParams.vrole = role;

            req.url = parsedUrl.pathname+"?"+querystring.stringify(queryParams);
            */

            //req.query.myQueryParam = 'valore_del_query_parametro';
            req.query.vuid = uid;
            req.query.vrole = role;
        }

        
        


        if (authUsers == null || authUsers.includes(role)) {
            // Puoi andare avanti
            next();
        } else {
            // Rispondi con status 403 Forbidden
            const timestamp = Date.now();
            res.status(401).json({ message: "Unauthorized", timestamp: timestamp, status: 401 });
        }

    });

    

}






// Middleware per verificare il token JWT
async function checkToken0(req, res, next) {

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

            // se il token e' scaduto e il token master e' ancora valido effettuto un refresh 
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



