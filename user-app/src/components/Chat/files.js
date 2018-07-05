import {Platform} from 'react-native';
import {URL} from '../Login/const';
import {getAuth, getAuthForMediaPost} from '../Login/auth';
import {setMsg, sendMessage} from './sendMessages';
import moment from 'moment';

var link = '';

export var type = '';

export function setType(asset) {
	type = asset;
}

export function uploadFile (uri, ticket) {
	
	var ext = '';
	if(type == 'Photos') {
		ext = '.jpg';
	}
	if(type == 'Videos') {
		ext = '.mp4';
		if (Platform.OS === 'ios') {
			ext = '.MOV';
		}
	}

	var filename = (btoa(moment()) + ext);
	
	//send  filename to chat
	fetch(URL + '/messages/' + ticket + '?attachment=' + filename, {
        method: 'POST',
        headers: getAuth(),
        body: filename
	})
	
	//convert file into FormData
	const image = {
		      uri: uri,
		      type: 'multipart/form-data',
		      name: filename
	}
	const imgBody = new FormData();
	imgBody.append('file', image);
	
	//send file to backend
	fetch(URL + '/files/' + ticket, {
        method: 'POST',
        headers: getAuthForMediaPost(),
        body: imgBody
	}).then(
		    response => {
		    	response.json();
		    	//console.log(JSON.stringify(response));
		    	if (response.status == '409') {
		    		alert("File upload failed. Filename already exists.");
		    	}
		    }
	  ).catch(
	    error => {
	    	console.log('uploadImage error:', error);
	    	alert("File upload failed.");
	    }
	  );
	
	console.log(ticket);
	console.log(imgBody);
}

export async function getDownloadLink (filename, ticket) {
	
	//get downloadlink from backend
	const res = await fetch(URL + '/files/' + ticket + '/' + filename + '?thumbnail=false', {
        method: 'GET',
        headers: getAuth()
	}).then(
		    response => {
		    	link = '';
		    	if (response.status == '200') {
		    		link = response.url;
		    	} else if (response.status == '404') {
		    		alert("File does not exist.");
		    	}
		    }
	  ).catch(
	    error => {
	    	console.log('downloadImage error:', error);
	    	alert("File download failed.");
	    }
	  );
	return link;	
}