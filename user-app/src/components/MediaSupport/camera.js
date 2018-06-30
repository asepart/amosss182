import React, {Component} from 'react';
import {Text, View} from 'react-native';
import styles from '../Login/Design';
import Camera from 'react-native-camera';
import uploadFile from '../Chat/files';
import ticket from '../Chat/sendMessages';

export var uri = '';

export default class CameraAccess extends Component { 

  constructor(props) {
    super(props);
    this.state = {
      path: null,
    };
  }  
    
render() {
return ( 
        <View style={styles.container}>
        <Camera 
        ref={(cam) => {
          this.camera = cam;
        }}
                     style={styles.view}
                     aspect={Camera.constants.Aspect.fill}
                     //captureTarget={Camera.constants.CaptureTarget.disk}
                     //permissionDialogTitle={'Permission to use camera'}
                     //permissionDialogMessage={'We need your permission to use your camera phone'}
                     >
        <Text style={styles.capture} onPress={this.takePicture.bind(this)}>[CAPTURE]</Text>
        <Text style={styles.capture}
        // onPress={this.sendPicture.bind(this)}
        >Send</Text>
       </Camera>
         </View>
       
   );
 }

  //takePicture() {
 // const options = {};
// this.camera.capture({metadata:options}).then((data) => 
//console.log(data))
//.catch(err => console.error(err));  
//}

//takePicture() {
//  if (this.camera) {
 //   const options = { quality: 0.5 };
  //  const data = this.camera.capture(options)
  //  console.log(data.uri);
//  }
//};

takePicture = async function(camera) {
  alert('data');
 var options = { quality: 0.5 };
 var data = await camera.takePictureAsync(options);
  this.setState({ path: data.uri });
    // this.props.updateImage(data.uri);
    // console.log('Path to image: ' + data.uri);
    console.log(data.uri);
    
};


//sendPicture() {
//uploadFile(uri, ticket)
//const { navigate } = this.props.navigation;
//		navigate("Seventh", { name: "GetMessages" });
//}

}