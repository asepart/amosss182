import React, { Component } from 'react';
import { Text, View, Button } from 'react-native';
import {getAuth} from '../shared/auth';
import {URL} from '../shared/const';
import {setUpdateBoolean} from '../shared/GlobalState';
import { Link } from 'react-router-dom';

export class File extends Component {
	constructor(props) {
		super(props);
	}

	deleteFile() {
		fetch(URL + '/tickets/' + this.props.name.ticketId + '/attachments/' + this.props.name.attachmentId, {
			method:'DELETE',
			headers: getAuth()
		})
		.then(window.location.reload())
		.catch((error) => {console.error(error);});
	}

	render() {
		return (
			<View style={{flexDireaction:'row'}}>
				<Text>
					<Link to={"/files/" + this.props.name.attachmentId }>
						Name:{this.props.name.originalName}
					</Link>
					<img src={require('../images/delete.png')} onClick={this.deleteFile.bind(this)} style={{height: 15, width: 15, marginBottom: -5}} alt="delete"/>
				</Text>
			</View>
		);
	}
}
