import React, { Component } from 'react';
import { Text } from 'react-native';
import { setState } from '../shared/GlobalState';
import { Link } from 'react-router-dom';

var pickerPlaceholder = "Category";

export default class ProjectButton extends Component {
	displayProject() {
		setState({
			isAuth: true,
			show: 'showTickets',
			param: this.props.proj.row.entryKey,
			name: this.props.proj.row.name,
		});
	}
	render() {
		return (
			<Link to={"/projects/" + this.props.proj.row.entryKey} style={{textDecoration: 'none'}}>
				<Text
					onPress = { this.displayProject.bind(this) }
					style={{color: '#5daedb'}}
				>
					{this.props.proj.row.name}
				</Text>
			</Link>
		);
	}
}
