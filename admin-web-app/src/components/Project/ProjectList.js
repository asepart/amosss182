import React, { Component } from 'react';
import {ActivityIndicator,Button, View} from 'react-native';
import ReactTable from 'react-table';
import {getAuth} from '../shared/auth';
import {URL} from '../shared/const';
import ProjectButton from './ProjectButton';
import UpdateProjectButton from './UpdateProjectButton';
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

	showUserManagement () {
		setState({
			isAuth: true,
			show: 'listUsers',
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
				Header: 'Name',
				accessor: 'projectName',
				Cell: props => <ProjectButton proj={props}/>
			}, {
				Header: 'Entry Code',
				accessor: 'entryKey' // String-based value accessors!
			}, {
				Header: '',
				accessor: '',
				Cell: props => <UpdateProjectButton proj={props}/>
			}/*, {	//TODO:
				Header: '',
				accessor: '',
				Cell: props => <DeleteProjectButton proj={props}/>
			}*/
		]

		return (
			<View>
				<Button
					disabled = "true"
					title = {"Projects"}
				/>
				<Button
					onPress = { this.showAddProject }
					title = "Add Project"
					color = "#0c3868"
				/>
				<Button
					onPress = { this.showUserManagement }
					title = "User Management"
					color = "#0c3868"
				/>
				<ReactTable data={this.state.dataSource} columns={columns}/>
			</View>
		);
	}
}
