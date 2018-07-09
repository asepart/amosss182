import {URL, cki} from '../shared/const';

var auth=false;
export var username='';
export var psw='';

export function setPSW(lpsw) {
	cki.set('psw', lpsw);
}
export function setUsername(lUsername) {
	cki.set('username', lUsername);
}

export function getAuth() {
	return {
		'Accept': 'application/json',
		'Authorization': 'Basic ' + btoa(username + ":" + psw)
	};
}

export function getAuthForPost() {
	return {
		'Accept': 'text/plain',
		'Content-Type': 'application/json; charset=utf-8',
		'Authorization': 'Basic ' + btoa(username + ":" + psw)
	};
}

export function getAuthForMediaPost() {
	return {
		'Authorization': 'Basic ' + btoa(username + ":" + psw)
	};
}

async function authenticate() {
	var response = await fetch(URL + '/login/admin', {
		method: 'GET',
		headers: getAuth()
	})
	switch (response.status) {
		case 200:
			auth=true;
			return true;
		case 401:
			auth=false;
			return false;
		default:
			console.error("Error:" + response.status);
			console.error(response.text);
	}
}

export async function isAuth() {;
	if (auth){
		return true;
	}
	if (username === '' || psw === ''){
		if(!(cki.get("username") === undefined) && typeof cki.get("username") === 'string'){
			username = cki.get("username");
		} else {
			return false;
		}
		if(!(cki.get("psw") === undefined) && typeof cki.get("psw") === 'string'){
			psw = cki.get("psw");
		} else {
			return false;
		}
	}
	if (typeof username === 'string' && typeof psw === 'string')
		return await authenticate();
	else {
		username = '';
		psw = '';
		return false;
	}
}
