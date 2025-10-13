# System Zarządzania Pracownikami (TechCorp)

Projekt implementuje podstawowy system zarządzania danymi pracowników z funkcjonalnościami analitycznymi, obsługą importu z CSV oraz integracją z zewnętrznym REST API.

## Wymagania wstępne

1.  **Java Development Kit (JDK):** Wersja 11 lub nowsza (wymagana ze względu na `HttpClient`).
2.  **Maven:** Zainstalowany i skonfigurowany.
3.  **Połączenie internetowe:** Wymagane do pobrania zależności oraz do integracji z REST API.

## Struktura projektu

Projekt jest podzielony na pakiety zgodnie z logiką:
* `model/`: Klasy danych (`Employee`, `Position`, `ImportSummary`, `CompanyStatistics`).
* `exception/`: Niestandardowe wyjątki (`ApiException`, `InvalidDataException`).
* `service/`: Logika biznesowa (`EmployeeService`, `ImportService`, `ApiService`).
* `Main.java`: Główna klasa demonstracyjna.

## Konfiguracja zależności

Projekt wymaga zewnętrznych bibliotek do obsługi CSV i JSON. Upewnij się, że plik `pom.xml` zawiera następujące zależności:

```xml
<dependencies>
    <dependency>
        <groupId>com.google.code.gson</groupId>
        <artifactId>gson</artifactId>
        <version>2.10.1</version>
    </dependency>
    <dependency>
        <groupId>com.opencsv</groupId>
        <artifactId>opencsv</artifactId>
        <version>5.12.0</version>
    </dependency>
</dependencies>