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
		alert(this.props.name);
	}

	render() {
		return (
			<View style={{flexDireaction:'row'}}>
				<Text>{this.props.name}
				<img src={require('../images/delete.png')} onClick={this.deleteFile.bind(this)} style={{height: 15, width: 15, marginBottom: -5}} alt="delete"/></Text>
			</View>
		);
	}
}
