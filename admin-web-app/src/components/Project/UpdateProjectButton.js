import React, { Component } from 'react';
import { Text } from 'react-native';
import { setState } from '../shared/GlobalState';
import { Link } from 'react-router-dom';

export default class UpdateProjectButton extends Component {
	displayProject() {
		setState({
			isAuth: true,
			show: 'addProject',
			param: this.props.proj.row.entryKey,
			name: this.props.proj.row.projectName
		});
	}
	render() {
		return (	// TODO: add edit icon instead of text here
			<Link to="/addProject" style={{textDecoration: 'none'}}>
				<Text
					onPress = { this.displayProject.bind(this) }
					style={{color: '#5daedb'}}
				>
					EDIT
				</Text> 
			</Link>
		);
	}
}
