import {URL} from '../Login/const';
import {getAuth} from '../Login/auth';

var key = "";
var val=false;

export function setKey(pkey) {
	key = pkey;
}


async function validateKey() {
	var response = await fetch(URL + '/join', {
        method: 'POST',
        headers: getAuth(),
        body:  key
    })
  
	switch (response.status) {
		case 200:
			val=true;
            return true;
        // user already joined    
        case 401:
            val=false;
            return false;
        // username or password is invalid    
        case 400:
            val=false;
            return false;
        // user is not part of the project    
        case 403:
            val=false;
            return false;
        // key is invalid    
        case 404:
            val=false;
            return false;
		default:
			console.error("Error:" + response.status);
			console.error(response.text);
	}
}

export async function isValid() {
	if (val)
		return true;
	if (key === '')
		return false;
	return await validateKey();
}