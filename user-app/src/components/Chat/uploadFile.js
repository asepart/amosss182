import {URL} from '../Login/const';
import {getAuthForMediaPost} from '../Login/auth';

export function uploadFile (imgBody, ticket) {
	fetch(URL + '/files/' + ticket, {
        method: 'POST',
        headers: getAuthForMediaPost(),
        body: imgBody
	}).then(
		    response => {
		    	response.json();
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