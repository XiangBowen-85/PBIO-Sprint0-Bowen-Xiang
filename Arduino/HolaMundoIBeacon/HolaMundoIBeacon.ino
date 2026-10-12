// -*-c++-*-

// --------------------------------------------------------------
//
// Jordi Bataller i Mascarell
// 2019-07-07
//
// --------------------------------------------------------------

// https://learn.sparkfun.com/tutorials/nrf52840-development-with-arduino-and-circuitpython

// https://stackoverflow.com/questions/29246805/can-an-ibeacon-have-a-data-payload

// --------------------------------------------------------------
// --------------------------------------------------------------
#include <bluefruit.h>  // 引入 Adafruit nRF52 专用的低功耗蓝牙底层库

#undef min  // vaya tela, están definidos en bluefruit.h y  !
#undef max  // colisionan con los de la biblioteca estándar

// --------------------------------------------------------------
// --------------------------------------------------------------
#include "LED.h"
#include "PuertoSerie.h"

// --------------------------------------------------------------
// --------------------------------------------------------------
namespace Globales {  // 定义全局对象

LED elLED(/* NUMERO DEL PIN LED = */ 7);  // 代表 7 号引脚连着板载 LED 灯

PuertoSerie elPuerto(/* velocidad = */ 115200);  // 115200 o 9600 o ...

// Serial1 en el ejemplo de Curro creo que es la conexión placa-sensor
// 我认为 Curro 示例中的 Serial1 是板载传感器的连接。
};

// --------------------------------------------------------------
// --------------------------------------------------------------
#include "EmisoraBLE.h"
#include "Publicador.h"
#include "Medidor.h"


// --------------------------------------------------------------
// --------------------------------------------------------------
namespace Globales {

Publicador elPublicador;  // 声明一个广播发布者

Medidor elMedidor;  // 声明一个测量计

};  // namespace

// --------------------------------------------------------------
// --------------------------------------------------------------
void inicializarPlaquita() {

  // de momento nada

}  // ()

// --------------------------------------------------------------
// setup()
// --------------------------------------------------------------
void setup() {

  Globales::elPuerto.esperarDisponible();

  Globales::elPuerto.escribir(
    "\n*** FIRMWARE XBW01 - 2026-10-12 ***\n");

  //
  //
  //
  inicializarPlaquita();

  // Suspend Loop() to save power
  // suspendLoop();

  //
  //
  //
  Globales::elPublicador.encenderEmisora();

  // Globales::elPublicador.laEmisora.pruebaEmision();

  //
  //
  //
  Globales::elMedidor.iniciarMedidor();

  //
  //
  //
  esperar(1000);

  Globales::elPuerto.escribir("---- setup(): fin ---- \n ");

}  // setup ()

// --------------------------------------------------------------
// --------------------------------------------------------------
inline void lucecitas() {
  using namespace Globales;

  elLED.brillar(100);             // 100 encendido
  esperar(400);                   //  100 apagado
  elLED.brillar(100);             // 100 encendido
  esperar(400);                   //  100 apagado
  Globales::elLED.brillar(100);   // 100 encendido
  esperar(400);                   //  100 apagado
  Globales::elLED.brillar(1000);  // 1000 encendido
  esperar(1000);                  //  100 apagado
}  // ()

// --------------------------------------------------------------
// loop ()
// --------------------------------------------------------------
namespace Loop {
uint8_t cont = 0;
};

// ..............................................................
// ..............................................................
void loop() {

  using namespace Loop;
  using namespace Globales;

  cont++;

  elPuerto.escribir("\n---- loop(): empieza ");
  elPuerto.escribir(cont);
  elPuerto.escribir("\n");


  lucecitas();

  //
  // mido y publico
  //
  int valorCO2 = elMedidor.medirCO2();

  elPublicador.publicarCO2(valorCO2,
                           cont,
                           3000  // intervalo de emisión
  );

  //
  // mido y publico
  //
  int valorTemperatura = elMedidor.medirTemperatura();

  elPublicador.publicarTemperatura(valorTemperatura,
                                   cont,
                                   3000  // intervalo de emisión
  );

  //
  // prueba para emitir un iBeacon y poner
  // en la carga (21 bytes = uuid 16 major 2 minor 2 txPower 1 )
  // lo que queramos (sin seguir dicho formato)
  //
  // Al terminar la prueba hay que hacer Publicador::laEmisora privado
  //
  /*
 * Prueba de carga libre desactivada para el Sprint 0.
 *
 * Esta prueba sobrescribía temporalmente los anuncios
 * de CO2 y temperatura.
 */
  /*
char datos[21] = {
    'H', 'o', 'l', 'a',
    'H', 'o', 'l', 'a',
    'H', 'o', 'l', 'a',
    'H', 'o', 'l', 'a',
    'H', 'o', 'l', 'a',
    'H'
};

elPublicador.laEmisora.emitirAnuncioIBeaconLibre(
    "MolaMolaMolaMolaMolaM",
    21
);

esperar(2000);

elPublicador.laEmisora.detenerAnuncio();
*/

  elPuerto.escribir("---- loop(): acaba **** ");
  elPuerto.escribir(cont);
  elPuerto.escribir("\n");

}  // loop ()
// --------------------------------------------------------------
// --------------------------------------------------------------
// --------------------------------------------------------------
// --------------------------------------------------------------
