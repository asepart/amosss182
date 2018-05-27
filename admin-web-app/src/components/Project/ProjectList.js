import React, { Component } from 'react';
import {ActivityIndicator,Button, View,Text} from 'react-native';
import ReactTable from 'react-table';
import {getAuth} from '../shared/auth';
import {URL} from '../shared/const';
import ProjectButton from './ProjectButton';
import UpdateProjectButton from './UpdateProjectButton';
import DeleteProjectButton from './DeleteProjectButton';
import { setState } from '../shared/GlobalState';
import 'react-table/react-table.css';
import '../../index.css';
import {Link} from 'react-router-dom'

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
	
	showProjectList () {
		setState({
			isAuth: true,
			show: ''
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
			}, {
				Header: '',
				accessor: '',
				Cell: props => <DeleteProjectButton proj={props}/>
			}
		]

		return (	// TODO: add home icon instead of text here
			<View>
				<View>
					<Link to="/" style={{textDecoration: 'none'}}>
					<Text
						onPress = { this.showProjectList.bind(this) }
						style={{color: '#5daedb'}}
					>
						HOME
					</Text>
					</Link>
				</View>
				<View>
					<Link to="/addproject" style={{textDecoration: 'none'}}>
					<Button
						onPress = { this.showAddProject }
						title = "Add Project"
						color = "#0c3868"
					/>
					</Link>
				</View>
				<View style={{flexDirection: 'row'}}>
					<View style={{flex:1}}>
						<Button
							onPress = { function doNothing() {} }
							disabled = {true}
							title = {"Projects"}
						/>
					</View>
					<View style={{flex:1}}>
						<Link to="/usermanagement" style={{textDecoration: 'none'}}>
						<Button
							onPress = { this.showUserManagement }
							title = "Users"
							color = "#0e4a80"
						/>
						</Link>
					</View>
				</View>
				<ReactTable data={this.state.dataSource} defaultPageSize={10} showPagination={false} columns={columns}/>
			</View>
		);
	}
}
