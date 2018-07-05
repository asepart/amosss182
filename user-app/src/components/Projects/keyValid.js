import {URL} from '../Login/const';
import {getAuth,username,psw} from '../Login/auth';

export var key = '';
var val=false;

window.btoa = require('Base64').btoa;

export function setKey(pkey) {
	key = pkey;
}



async function validateKey() {
var response = await fetch(URL + '/join', {
        method: 'POST',
        headers: getAuth(),
        body:  key
    }).catch( (error) => {
    	console.error("Error:" + response.status);
    	console.error(response.text);}
    );
	switch (response.status) {
		case 200:
			val=true;
        case 204:
        	val=true;
        // username or password is invalid    
        case 401:
            val=false;
        // user had already joined 
        case 400:
            val=true;
        // user is not part of the project    
        case 403:
            val=false;
        // key is invalid    
        case 404:
            val=false;
		default:			
	}
	return val;
}

export async function isValid() {
    if (key === '')
		return false;
	return await validateKey();
}