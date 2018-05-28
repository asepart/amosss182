import Cookies from 'universal-cookie';

export const cki = new Cookies(document.cookie);
export const URL = getURL();

function getURL(){
	if (window.location.hostname.indexOf("asepartweb-dev.herokuapp") !== -1){
		return "https://asepartback-dev.herokuapp.com";
	} 
	
	else if (window.location.hostname.indexOf("asepartweb.herokuapp") !== -1){
		return "https://asepartback.herokuapp.com";
	}
	
	else return "http://localhost:12345";
}
