import React, { Component } from 'react';
import {ActivityIndicator,Button, View} from 'react-native';
import ReactTable from 'react-table';
import {getAuth} from '../shared/auth';
import {URL} from '../shared/const';
import ProjectButton from './ProjectButton';
import { setState } from '../shared/GlobalState';
import 'react-table/react-table.css';
import '../../index.css';

export default class ProjectList extends Component {
	constructor(props) {
		super(props);
		this.state = {
			isLoading: true
		}
	}

	componentDidMount() {
		return fetch(URL + '/projects', {method:'GET', headers: getAuth()})
		.then((response) => response.json())
		.then((responseJson) => {
			this.setState({
				isLoading: false,
				dataSource: responseJson
			}, function() {});
		}).catch((error) => {
			console.error(error);
		});
	}

	showAddProject () {
		setState({
			isAuth: true,
			show: 'addProject',
			param: ''
		});
	}

	render() {
		if (this.state.isLoading) {
			return (
				<View style={{flex: 1,padding: 20}}>
					<ActivityIndicator/>
				</View>
			)
		}

		const columns = [
			{
				Header: 'Project Name',
				accessor: 'projectName',
				Cell: props => <ProjectButton proj={props.value}/>
			}, {
				Header: 'Entrycode',
				accessor: 'entryKey' // String-based value accessors!
			}
		]

		return (
			<View>
				<Button
					onPress = { this.showAddProject }
					title = "Add Project"
					color = "#841584"
				/>
				<ReactTable data={this.state.dataSource} columns={columns}/>
			</View>
		);
	}
}
