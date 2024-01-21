ARCore SDK for Android
======================
Copyright 2017 Google LLC

This SDK provides APIs for all of the essential AR features like motion
tracking, environmental understanding, and light estimation. With these
capabilities you can build entirely new AR experiences or enhance existing apps
with AR features.


## Breaking change affecting previously published 32-bit-only apps

_Google Play Services for AR_ (ARCore) has removed support for 32-bit-only
ARCore-enabled apps running on 64-bit devices. Support for 32-bit apps running
on 32-bit devices is unaffected.

If you have published a 32-bit-only (`armeabi-v7a`) version of your
ARCore-enabled app without publishing a corresponding 64-bit (`arm64-v8a`)
version, you must update your app to include 64-bit native libraries.
32-bit-only ARCore-enabled apps that are not updated by this time may crash when
attempting to start an augmented reality (AR) session.

To learn more about this breaking change, and for instructions on how to update
your app, see https://developers.google.com/ar/64bit.


## Quick Start

See the [Quickstart for Android Java](//developers.google.com/ar/develop/java/quickstart)
or [Quickstart for Android NDK](//developers.google.com/ar/develop/c/quickstart)
developer guide.


## API Reference

See the [ARCore SDK for Java API Reference](//developers.google.com/ar/reference/java)
or [ARCore SDK for C API Reference](//developers.google.com/ar/reference/c).


## Release Notes

The SDK release notes are available on the
[releases](//github.com/google-ar/arcore-android-sdk/releases) page.


## Terms & Conditions

By downloading the ARCore SDK for Android, you agree that the
[**ARCore Additional Terms of Service**](https://developers.google.com/ar/develop/terms)
governs your use thereof.


## User privacy requirements

You must disclose the use of Google Play Services for AR (ARCore) and how it
collects and processes data, prominently in your application, easily accessible
to users. You can do this by adding the following text on your main menu or
notice screen: "This application runs on [Google Play Services for AR](//play.google.com/store/apps/details?id=com.google.ar.core) (ARCore),
which is provided by Google LLC and governed by the [Google Privacy Policy](//policies.google.com/privacy)".

See the [User privacy requirements](https://developers.google.com/ar/develop/privacy-requirements).

## Deprecation policy

Apps built with **ARCore SDK 1.12.0 or higher** are covered by the
[Cloud Anchor API deprecation policy](//developers.google.com/ar/distribute/deprecation-policy).

Apps built with **ARCore SDK 1.11.0 or lower** will be unable to host or resolve
Cloud Anchors beginning December 2020 due to the SDK's use of an older,
deprecated ARCore Cloud Anchor service.





## Exposé 

Erleichterte Anwendung von Augmented Reality durch Nutzung faltbarer Android Smartphones 

 

Problemstellung: Auf herkömmlichen Smartphones gestaltet sich die Platzierung virtueller Objekte mittels Augmented Reality schwierig, da man für einige Operationen Mehrfingergestenerkennung benötigt. Diese Gesten setzen voraus, dass das verwendete Smartphone über präzisen Multitouch verfügt, weil die Platzierung ansonsten ungenau wird. Des Weiteren werden die Operationen auf demselben Bildschirm vorgenommen, auf welchem das Kamerabild dargestellt wird. Hierdurch ergibt sich die weitere Problematik, dass relevante Bereiche des Bilds verdeckt werden können. 

Zielsetzungen: Dieses Problem soll mithilfe faltbarer Smartphones umgangen werden, indem der untere Abschnitt des Displays als Platzierungshilfe genutzt wird und auf dem oberen Abschnitt das 
reguläre Kamerabild erscheint. 
Hierdurch sollen sich eine präzisere Bewegung des Objekts, sowie, bedingt durch limitierte Fingergesten, erweiterte Handlungsmöglichkeiten ergeben. 

Forschungsstand und aktuelle Technologien: Aufgrund vieler Anbieter auf dem Markt, wurden Standards für Augmented Reality entwickelt. Diese umfassen verschiedene Hard- und Softwarekomponenten, wie technische Frameworks, die Integration, technische Anforderungen, 
Leistungs-, sowie Sicherheitsanforderungen (IEEE SA, 2023). 
Des Weiteren gibt es eigens von Google entwickelte und für Android genutzte Technologien für Augmented Reality, welche Entwicklern zur Erstellung hochwertiger Programme dienen. 

ARCore: Dieses Framework bietet grundlegende Tools zur Erstellung von AR- Funktionen. Diese beinhalten Bewegungserkennung, Anker zum zeitlichen Verlauf eines Objekts, Umwelterkennung, Tiefenverständnis und Lichtschätzung (Grundlegende Konzepte, 2022). Zudem kann ARCore auch auf iOS-Geräten genutzt werden (Ar in der ios-app aktivieren, 2023). 

Technische Herangehensweise: Siehe Zielsetzungen. Weiterhin:                                                    	    
Da ein einzelner Bildschirm auf zwei Dimensionen begrenzt ist, werden Fingergesten oder ein Schiebregler benötigt, um das Objekt nach vorne oder hinten zu bewegen. Dieses Problem soll durch die Nutzung des unteren Parts des Bildschirms beseitigt werden. Auf dem oberen Part kann man das Objekt in X und Z Richtung bewegen und auf dem unteren in X und Y Richtung. (Hierdurch werden zudem Fingergesten frei, welche für andere Operationen verwendet werden können.) Die Nutzung von zwei Bildschirmen bietet ergänzend die Möglichkeit weiterer Visualisierungen. Eine Möglichkeit besteht darin, auf dem unteren Bildschirm ein Koordinatensystem darzustellen. Alternativ kann ein Abbild des aufgenommenen Bildes aus der Vogelperspektive generiert werden, wodurch eine natürlichere Wahrnehmung des Objekts stattfinden kann. Dies erfordert jedoch eine höhere Rechenleistung des genutzten Geräts. Der untere Bildschirm kann des Weiteren dafür genutzt werden, weitere Visualisierungsfunktionen, wie einen Schiebregler zum Drehen des Objekts, einzubinden, wodurch Operationen ausgelagert werden und der Fokus des oberen Bildschirms ausschließlich auf dem dargestellten Objekt liegt. 

Ergebnisse: Ziel des Projekts ist es, einen vollständig funktionsfähigen Prototypen zu erstellen. 
Dieser soll dazu genutzt werden, Augmented Reality auf faltbaren Smartphones zu nutzen und weiter anzupassen. Da dieses Projekt spezifisch auf faltbare Smartphones angepasst ist, kann es dazu dienen, die Vorteile dieser Geräte im Hinblick auf reguläre Smartphones aufzuzeigen. 
Weil der Marktanteil faltbarer Smartphones von 1,6% im Jahr 2023 auf über 5% im Jahr 2027 steigen soll (Fan & Lu, 2023), sowie die jährlichen Einnahmen durch Augmented Reality im Jahr 2022 bei 62,75mrd $ lagen und für das Jahr 2030 eine Steigerung auf 1.109,71mrd $ prognostiziert wird (Augmented reality market size, 2023), wird es für Unternehmen immer relevanter, diese beiden Bereiche effizient zu kombinieren und spezifisch zugeschnittene Anwendungen zu entwickeln. Hierbei kann der entworfene Prototyp Einblicke in die kombinierte Nutzung beider Bereiche gewähren. 

Projektablauf: dieser Ablauf gilt lediglich der rudimentären Orientierung und wird sich voraussichtlich im Laufe des Projekts ändern. 

Erstellen eines Exposés 

Genaueres Bekanntmachen mit dem Projekt 

Festlegen der Sprechzeiten bezüglich Austauschs zu aktuellem Bearbeitungsstand 

Bekanntmachen mit den Frameworks und der Hardware 

Prototyp erstellen 

Gegebenenfalls Anpassungen des Prototyps vornehmen 

 


Literaturverzeichnis 

Ar in der ios-app aktivieren (2023) developers.google.com. Available at: https://developers.google.com/ar/develop/ios/enable-arcore?hl=de (Accessed: 02 October 2023).  

Augmented reality market size, share & covid-19 impact analysis, by Industry (Gaming, media, automotive, retail, healthcare, education, manufacturing, Real Estate/Architecture/Interior Design, Defense & Aerospace, Art & Designing, law enforcement, and others), by application (interactive showroom, driving experience, brand engagement, Space Visualization and virtual tours, augmented surgeries, medical education, drug information, well being, elearning apps, professional training, developing & Designing, Quality Control and others), and Regional Forecast, 2023-2030 (2023) Augmented Reality Market Size, Share, Trends | Forecast, 2030. Available at: https://www.fortunebusinessinsights.com/augmented-reality-ar-market-102553 (Accessed: 03 October 2023).  

Fan, B. and Lu, R. (2023) Press center - foldable smartphone market penetration estimated at 1.6% in 2023, with potential to exceed 5% by 2027, TrendForce. Available at: https://www.trendforce.com/presscenter/news/20230913-11845.html (Accessed: 03 October 2023).  

Grundlegende Konzepte (2022) developers.google.com. Available at: https://developers.google.com/ar/develop/fundamentals?hl=de (Accessed: 02 October 2023).  

IEEE SA - IEEE Approved Draft Standard for Augmented Reality on mobile devices: General requirements for software framework, components, and Integration (2023) IEEE Standards Association. Available at: https://standards.ieee.org/ieee/2048.101/10390/ (Accessed: 03 October 2023). 

 