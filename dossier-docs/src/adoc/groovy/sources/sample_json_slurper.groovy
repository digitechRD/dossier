import groovy.json.JsonSlurper

JsonSlurper jsonParser = new JsonSlurper()

// weatherResponse a été récupéré précédemment, via un appel aux APIs *openweater*
def parsedJson = jsonParser.parseText(weatherResponse)

for(int i=0; i<parsedJson.list.size; ++i) {
  def dayData = parsedJson.list[i]
  OpenWeatherDay newDay = new OpenWeatherDay(i)
  newDay.desc = dayData.weather[0].description
  newDay.icon = dayData.weather[0].icon
  newDay.tempDay = dayData.main.temp
  newDay.tempMin = dayData.main.temp_min
  newDay.tempMax = dayData.main.temp_max
  newDay.windSpeed = dayData.wind.speed

  // ok, l'instance *newDay* peut être stockée pour un traitement ultérieur (affichage, ...)
}

/**
 * inner class permettant de stocker les données relatives à la météo d'une journée
 */
class OpenWeatherDay {
  final int dayIdx
  String desc
  String icon
  int tempMin
  int tempMax
  int tempDay
  int windSpeed

  OpenWeatherDay(int dayIdx) {
    this.dayIdx = dayIdx
  }
}