import { StyleSheet} from 'react-native';

export default styles=StyleSheet.create({

  container: {
      flex: 1,
      padding: 20,
      backgroundColor: '#0c3868',

    },

  containerScroll: {
    flex:1,
    backgroundColor: '#0c3868',
  },  

    container2: {
      flex: 1,
      justifyContent: 'space-between',
      padding: 20,
      backgroundColor: '#0c3868',

    },
    
    containerPicker: {
        flex: 1,
        padding: 20,
        backgroundColor: '#0c3868',
        //alignItems moves items to upper center
        alignItems: 'center',
        //justifyContent moves items to center of page
        justifyContent: 'center'
      },

    containerAlign: {
      flex: 1,
      padding: 20,
      backgroundColor: '#0c3868',
      //alignItems moves items to upper center
      alignItems: 'center',
      //justifyContent moves items to center of page
      justifyContent: 'center'

    },
    containerBottomAlign: {
      flex: 1,
      padding: 20,
      backgroundColor: '#0c3868',
      //alignItems moves items to buttom
      alignItems: 'baseline',
      //justifyContent moves items to center of page
      justifyContent: 'center'

    },

    containerButtonRow: {
      flexDirection: 'row',
      justifyContent: 'space-between',
      backgroundColor: '#0c3868',
     
    },

    buttonRowContainer: {
      backgroundColor: '#009fca',
      paddingVertical: 15,
      width: '40%',
      marginBottom: 15,
      alignItems: 'center',
      justifyContent: 'center'
    },

    //Text input boxes
    input: {
      height: 40,
      width: 200,
      color: '#FFF',
      backgroundColor: '#14639e',
      marginTop: 10,
      paddingHorizontal: 10
    },
    // Text input box span over whole horizontal
    inputLong: {
      height: 40,
      width: 350,
      color: '#FFF',
      backgroundColor: '#14639e',
      marginBottom: 10,
      paddingVertical: 10,
      paddingHorizontal: 10
    },
    
  //Picker input boxes
    inputPickerIOS: {
      height: 200,
      width: 200,
      backgroundColor: '#14639e',
      marginBottom: 10,
      paddingHorizontal: 10
    },
  //Picker input boxes
    inputPicker: {
      height: 40,
      width: 200,
      backgroundColor: '#14639e',
      marginBottom: 10,
      paddingHorizontal: 10
    },
    //normal button
    buttonContainer: {
      backgroundColor: '#009fca',
      paddingVertical: 15,
      marginTop: 15,
      width: 200

    },
    //button spanning whole horizontal
    buttonMediumContainer: {
      backgroundColor: '#009fca',
      paddingVertical: 15,
      width: 320,
      marginBottom: 15
    },
    
    buttonLargeContainer: {
        backgroundColor: '#009fca',
        paddingVertical: 15,
        width: 350,
        marginBottom: 15
      },

    buttonText: {
      textAlign: 'center',
      color: '#FFF',
      fontWeight: '700'
    },

    buttonAcceptedContainer: {
      backgroundColor: '#64dd17',
      flex: 1,
      paddingVertical: 15,
      marginBottom: 15,
      justifyContent: 'space-between',
      width: 350
    },

    buttonProcessedContainer: {
      backgroundColor: '#ef6c00',
      paddingVertical: 15,
      flex: 1,
      marginBottom: 15,
      justifyContent: 'space-between',
      width: 350
    },

    buttonFinishedContainer: {
      backgroundColor: '#dd2c00',
      paddingVertical: 15,
      flex: 1,
      marginBottom: 15,
      justifyContent: 'space-between',
      width: 350
    },


    //add button
    addButton: {
       alignItems:'center',
       justifyContent:'center',
       width:60,
       height:60,
       backgroundColor:'#009fca'

    },

    //for items fixed at bottom
    bottomView:{
      flex: 1,
      padding: 20,
      backgroundColor: '#0c3868', 
      position: 'absolute',
      bottom: 0
    },

    error: {
      color: 'red'
    },
    
    success: {
        color: 'green'
      },

    textLarge: {
     // textAlign: 'center',
      color: '#FFF',
      fontSize: 20
    },

    text: {
      color: '#FFF',
      fontSize: 15
    },

    textBold: {
      color: '#FFF',
      fontSize: 15,
      fontWeight: 'bold'
    },

    testText: {
      color: '#FFF',
      fontSize: 30,
    },

    icon: {
      width:200,
      height:100
    },

    containerChat: {
        flex: 1,
        paddingLeft: 5,
        paddingRight: 5,
        backgroundColor: '#0c3868',
    },

    sendTextInput: {
      height: 40,
      flex: 3,
      alignItems: 'baseline',
      color: '#FFF',
      backgroundColor: '#14639e',
      marginBottom: 10,
      paddingVertical: 10,
      paddingHorizontal: 10
    },

    sendButton: {
      flex: 1,
      height: 40,
      backgroundColor: '#009fca',
      paddingVertical: 10,
    },
    view: {
      flex: 1,
      justifyContent: 'flex-end',
      alignItems: 'center'
    },
    
    capture: {
      flex: 0,
      backgroundColor: '#fff',
      borderRadius: 5,
      color: '#000',
      padding: 10,
      margin: 40
    }

  });
