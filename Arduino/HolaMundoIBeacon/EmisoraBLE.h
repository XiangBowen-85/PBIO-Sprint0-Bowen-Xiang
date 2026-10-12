// -*- mode: c++ -*-

// ----------------------------------------------------------
// Jordi Bataller i Mascarell
// 2019-07-07
// ----------------------------------------------------------
#ifndef EMISORA_H_INCLUIDO
#define EMISORA_H_INCLUIDO

// Buena introducción: https://learn.adafruit.com/introduction-to-bluetooth-low-energy/gap
// https://os.mbed.com/blog/entry/BLE-Beacons-URIBeacon-AltBeacons-iBeacon/

// fuente: https://www.instructables.com/id/Beaconeddystone-and-Adafruit-NRF52-Advertise-Your-/
// https://github.com/nkolban/ESP32_BLE_Arduino/blob/master/src/BLEBeacon.h

// https://os.mbed.com/blog/entry/BLE-Beacons-URIBeacon-AltBeacons-iBeacon/
// https://learn.adafruit.com/bluefruit-nrf52-feather-learning-guide/bleadvertising

// ----------------------------------------------------------
// ----------------------------------------------------------
#include "ServicioEnEmisora.h"

// ----------------------------------------------------------
// ----------------------------------------------------------
class EmisoraBLE {
private:

	const char* nombreEmisora;
	const uint16_t fabricanteID;
	const int8_t txPower;

public:

	// .........................................................
	// .........................................................
	using CallbackConexionEstablecida = void(uint16_t connHandle);
	using CallbackConexionTerminada = void(uint16_t connHandle, uint8_t reason);

	// .........................................................
	// .........................................................
	EmisoraBLE(const char* nombreEmisora_, const uint16_t fabricanteID_,
	           const int8_t txPower_)
	  : nombreEmisora(nombreEmisora_),
	    fabricanteID(fabricanteID_),
	    txPower(txPower_) {
		// no encender ahora la emisora, tal vez sea por el println()
		// que hace que todo falle si lo llamo en el contructor
		// ( = antes que configuremos Serial )
		// No parece que sea por el println,
		// por tanto NO_encenderEmisora();
	}  // ()

	// .........................................................
	// .........................................................
	/* creo que no me sirve esta versión porque parece
	 que no se instalen los callbacks si la emisora no está encendida,
	 pero no la puedo encender en el constructor 
  EmisoraBLE( const char * nombreEmisora_, const uint16_t fabricanteID_,
			  const int8_t txPower_,
			  CallbackConexionEstablecida cbce,
			  CallbackConexionTerminada cbct
			  ) 
	:
	EmisoraBLE ( nombreEmisora_, fabricanteID_, txPower_ )
  {
	instalarCallbackConexionEstablecida( cbce );
	instalarCallbackConexionTerminada( cbct );
  } // ()
  */

	// .........................................................
	// .........................................................
	void encenderEmisora() {
		// Serial.println ( "Bluefruit.begin() " );
		Bluefruit.begin();

		// por si acaso:
		(*this).detenerAnuncio();
	}  // ()

	// .........................................................
	// .........................................................
	void encenderEmisora(CallbackConexionEstablecida cbce,
	                     CallbackConexionTerminada cbct) {

		encenderEmisora();

		instalarCallbackConexionEstablecida(cbce);
		instalarCallbackConexionTerminada(cbct);

	}  // ()

	// .........................................................
	// .........................................................
	void detenerAnuncio() {

		if ((*this).estaAnunciando()) {
			// Serial.println ( "Bluefruit.Advertising.stop() " );
			Bluefruit.Advertising.stop();
		}

	}  // ()

	// .........................................................
	// estaAnunciando() -> Boleano
	// .........................................................
	bool estaAnunciando() {
		return Bluefruit.Advertising.isRunning();
	}  // ()

	// .........................................................
	// .........................................................
	void emitirAnuncioIBeacon(
	  uint8_t* beaconUUID,
	  int16_t major,
	  int16_t minor,
	  uint8_t rssi) {
		/*
     * Detenemos el anuncio anterior antes de preparar
     * la nueva medida.
     */
		(*this).detenerAnuncio();

		/*
     * Eliminamos los datos del anuncio anterior.
     */
		Bluefruit.Advertising.clearData();
		Bluefruit.ScanResponse.clearData();

		Bluefruit.setTxPower(
		  (*this).txPower);

		Bluefruit.setName(
		  (*this).nombreEmisora);

		/*
     * El nombre XBW01 se envía en la respuesta al escaneo.
     * La recepción del nombre no es necesaria para interpretar
     * la medida, porque la aplicación utilizará el UUID.
     */
		Bluefruit.ScanResponse.addName();

		/*
     * Estructura de datos iBeacon:
     *
     * 4C 00  -> identificador de fabricante Apple
     * 02 15  -> tipo iBeacon y longitud de la carga
     * UUID   -> 16 bytes
     * major  -> 2 bytes, big endian
     * minor  -> 2 bytes, big endian
     * RSSI   -> 1 byte
     *
     * Total: 25 bytes.
     */
		uint8_t datosIBeacon[25];

		datosIBeacon[0] = 0x4C;
		datosIBeacon[1] = 0x00;
		datosIBeacon[2] = 0x02;
		datosIBeacon[3] = 0x15;

		/*
     * Copiamos el UUID:
     * EPSG-GTI-PROY-3A
     */
		memcpy(
		  &datosIBeacon[4],
		  beaconUUID,
		  16);

		/*
     * Major en orden big endian.
     */
		datosIBeacon[20] =
		  (major >> 8) & 0xFF;

		datosIBeacon[21] =
		  major & 0xFF;

		/*
     * Minor en orden big endian.
     *
     * La conversión a uint16_t conserva correctamente
     * el complemento a dos para temperaturas negativas.
     */
		uint16_t minorBits =
		  (uint16_t)minor;

		datosIBeacon[22] =
		  (minorBits >> 8) & 0xFF;

		datosIBeacon[23] =
		  minorBits & 0xFF;

		/*
     * RSSI calibrado.
     */
		datosIBeacon[24] = rssi;

		/*
     * Añadimos las banderas BLE generales.
     */
		Bluefruit.Advertising.addFlags(
		  BLE_GAP_ADV_FLAGS_LE_ONLY_GENERAL_DISC_MODE);

		/*
     * Añadimos directamente los datos de fabricante.
     *
     * No usamos BLEBeacon en esta versión para evitar
     * dependencias del formato interno de la biblioteca.
     */
		Bluefruit.Advertising.addData(
		  BLE_GAP_AD_TYPE_MANUFACTURER_SPECIFIC_DATA,
		  datosIBeacon,
		  sizeof(datosIBeacon));

		Bluefruit.Advertising.restartOnDisconnect(
		  true);

		Bluefruit.Advertising.setInterval(
		  100,
		  100);

		/*
     * Iniciamos el anuncio hasta que Publicador
     * llame a detenerAnuncio().
     */
		Bluefruit.Advertising.start(0);
	}

	// .........................................................
	//
	// Ejemplo de Beacon (31 bytes)
	//
	// https://os.mbed.com/blog/entry/BLE-Beacons-URIBeacon-AltBeacons-iBeacon/
	//
	// The iBeacon Prefix contains the hex data : 0x0201061AFF004C0215. This breaks down as follows:
	//
	// 0x020106 defines the advertising packet as BLE General Discoverable and BR/EDR high-speed incompatible.
	// Effectively it says this is only broadcasting, not connecting.
	//
	// 0x1AFF says the following data is 26 bytes long and is Manufacturer Specific Data.
	//
	// 0x004C is Apple’s Bluetooth Sig ID and is the part of this spec that makes it Apple-dependent.
	//
	// 0x02 is a secondary ID that denotes a proximity beacon, which is used by all iBeacons.
	//
	// 0x15 defines the remaining length to be 21 bytes (16+2+2+1).
	//
	// Por ejemmplo:
	//
	// 1. prefijo: 9bytes
	//       0x02, 0x01, 0x06,       // advFlags 3bytes
	//       0x1a, 0xff,             // advHeader 2 (0x1a = 26 = 25(lenght de 0x4c a 0xca)+1)   0xFF -> BLE_GAP_AD_TYPE_MANUFACTURER_SPECIFIC_DATA
	//       0x4c, 0x00,             // companyID 2bytes
	//       0x02,                   // ibeacon type 1 byte
	//       0x15,                   // ibeacon length 1 byte (dec=21 lo que va a continuación: desde  la 'f' hasta 0x01)
	//
	// 2. uuid: 16bytes
	// 'f', 'i', 's', 't', 'r', 'o', 'f', 'i', 's', 't', 'r', 'o', 0xa7, 0x10, 0x96, 0xe0
	//
	// 2 major: 2bytes
	// 0x04, 0xd2,
	//
	// minor: 2bytes
	// 0x10, 0xe1,
	//
	// 0xca, // tx power : 1bytes
	//
	// 0x01, // este es el byte 31 = BLE_GAP_ADV_SET_DATA_SIZE_MAX, parece que sobra
	//
	// .........................................................
	// Para enviar como carga libre los últimos 21 bytes de un iBeacon (lo que normalmente sería uuid-16 major-2 minor-2 txPower-1)
	// .........................................................
	/*
  void emitirAnuncioIBeaconLibre( const char * carga ) {

	const uint8_t tamanyoCarga = strlen( carga );
  */
	void emitirAnuncioIBeaconLibre(const char* carga, const uint8_t tamanyoCarga) {

		(*this).detenerAnuncio();

		Bluefruit.Advertising.clearData();
		Bluefruit.ScanResponse.clearData();  // hace falta?

		// Bluefruit.setTxPower( (*this).txPower ); creo que no lo pongo porque es uno de los bytes de la parte de carga que utilizo
		Bluefruit.setName((*this).nombreEmisora);
		Bluefruit.ScanResponse.addName();

		Bluefruit.Advertising.addFlags(BLE_GAP_ADV_FLAGS_LE_ONLY_GENERAL_DISC_MODE);

		// con este parece que no va  !
		// Bluefruit.Advertising.addFlags(BLE_GAP_ADV_FLAG_LE_GENERAL_DISC_MODE);

		//
		// hasta ahora habrá, supongo, ya puestos los 5 primeros bytes. Efectivamente.
		// Falta poner 4 bytes fijos (company ID, beacon type, longitud) y 21 de carga
		//
		uint8_t restoPrefijoYCarga[4 + 21] = {
			0x4c, 0x00,  // companyID 2
			0x02,        // ibeacon type 1byte
			21,          // ibeacon length 1byte (dec=21)  longitud del resto // 0x15 // ibeacon length 1byte (dec=21)  longitud del resto
			'-', '-', '-', '-',
			'-', '-', '-', '-',
			'-', '-', '-', '-',
			'-', '-', '-', '-',
			'-', '-', '-', '-',
			'-'
		};

		//
		// addData() hay que usarlo sólo una vez. Por eso copio la carga
		// en el anterior array, donde he dejado 21 sitios libres
		//
		memcpy(&restoPrefijoYCarga[4], &carga[0], (tamanyoCarga > 21 ? 21 : tamanyoCarga));

		//
		// copio la carga para emitir
		//
		Bluefruit.Advertising.addData(BLE_GAP_AD_TYPE_MANUFACTURER_SPECIFIC_DATA,
		                              &restoPrefijoYCarga[0],
		                              4 + 21);

		//
		// ? qué valores poner aquí ?
		//
		Bluefruit.Advertising.restartOnDisconnect(true);
		Bluefruit.Advertising.setInterval(100, 100);  // in unit of 0.625 ms

		Bluefruit.Advertising.setFastTimeout(1);  // number of seconds in fast mode
		//
		// empieza el anuncio, 0 = tiempo indefinido (ya lo pararán)
		//
		Bluefruit.Advertising.start(0);

		Globales::elPuerto.escribir("emitiriBeacon libre  Bluefruit.Advertising.start( 0 );  \n");
	}  // ()

	// .........................................................
	// .........................................................
	bool anyadirServicio(ServicioEnEmisora& servicio) {

		Globales::elPuerto.escribir(" Bluefruit.Advertising.addService( servicio ); \n");

		bool r = Bluefruit.Advertising.addService(servicio);

		if (!r) {
			Serial.println(" SERVICION NO AÑADIDO \n");
		}


		return r;
		// nota: uso conversión de tipo de servicio (ServicioEnEmisora) a BLEService
		// para addService()
	}  // ()


	// .........................................................
	// .........................................................
	bool anyadirServicioConSusCaracteristicas(ServicioEnEmisora& servicio) {
		return (*this).anyadirServicio(servicio);
	}  //

	// .........................................................
	template<typename... T>
	bool anyadirServicioConSusCaracteristicas(ServicioEnEmisora& servicio,
	                                          ServicioEnEmisora::Caracteristica& caracteristica,
	                                          T&... restoCaracteristicas) {

		servicio.anyadirCaracteristica(caracteristica);

		return anyadirServicioConSusCaracteristicas(servicio, restoCaracteristicas...);

	}  // ()

	// .........................................................
	template<typename... T>
	bool anyadirServicioConSusCaracteristicasYActivar(ServicioEnEmisora& servicio,
	                                                  // ServicioEnEmisora::Caracteristica & caracteristica,
	                                                  T&... restoCaracteristicas) {

		bool r = anyadirServicioConSusCaracteristicas(servicio, restoCaracteristicas...);

		servicio.activarServicio();

		return r;

	}  // ()

	// .........................................................
	// .........................................................
	void instalarCallbackConexionEstablecida(CallbackConexionEstablecida cb) {
		Bluefruit.Periph.setConnectCallback(cb);
	}  // ()

	// .........................................................
	// .........................................................
	void instalarCallbackConexionTerminada(CallbackConexionTerminada cb) {
		Bluefruit.Periph.setDisconnectCallback(cb);
	}  // ()

	// .........................................................
	// .........................................................
	BLEConnection* getConexion(uint16_t connHandle) {
		return Bluefruit.Connection(connHandle);
	}  // ()

};  // class

#endif

// ----------------------------------------------------------
// ----------------------------------------------------------
// ----------------------------------------------------------
// ----------------------------------------------------------
