#include <wiringPi.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <parse.h>
#define MAXTIMINGS	85
#define PIN		7
int data[5] = { 0, 0, 0, 0, 0 };
int readingOK=0;
long int DELAY = 10000;//In Microseconds
const char PARSE_APP_ID[] = "LsrJX7KIwS54yaVaPxQk9KgkLWmlfug0ZAdIQIN6";
const char PARSE_CLIENT_KEY[] = "xgavPnf4nCCOakOSHF6xh5OjVIkO0Bqd0On177SB";

 
void readData()
{
	uint8_t laststate	= HIGH;
	uint8_t counter		= 0;
	uint8_t j		= 0, i;
	data[0]=data[1]=data[2]=data[3]=data[4]=0;
 	pinMode( PIN, OUTPUT );
	digitalWrite( PIN, LOW );
	delay( 18 );
	digitalWrite( PIN, HIGH );
	delayMicroseconds( 40 );
	pinMode( PIN, INPUT );
 	for ( i = 0; i < MAXTIMINGS; i++ )
	{
		counter = 0;
		while ( digitalRead( PIN ) == laststate )
		{
			counter++;
			delayMicroseconds( 1 );
			if ( counter == 255 )
			{
				break;
			}
		}
		laststate = digitalRead( PIN );
 
		if ( counter == 255 )
			break;
		if ( (i >= 4) && (i % 2 == 0) )
		{
			data[j / 8] <<= 1;
			if ( counter > 16 )
				data[j / 8] |= 1;
			j++;
		}
	}
	if ( (j >= 40) &&
	     (data[4] == ( (data[0] + data[1] + data[2] + data[3]) & 0xFF) ) )
	{
		printf( "Humidity = %d.%d %% ||| Temperature = %d.%d *C\n",data[0], data[1], data[2], data[3] );
		readingOK = 1;
	}else  {
		printf( "Bad Data ; Retrying\n" );
		readingOK = 0;
	}
}
 
int main( void )
{
	ParseClient client = parseInitialize(PARSE_APP_ID, PARSE_CLIENT_KEY);
 	printf( "Begin Temperature Monitor\n" );
 	char string[100];
 
	if ( wiringPiSetup() == -1 )
		exit( 1 );

 	begin:
	while ( 1 )
	{
		readData();
		if(readingOK==0)
		{
			goto begin;
		}
		else if(readingOK==1)
		{	
			snprintf(string, sizeof(string), "{\"temp\":%d.%d,\"humid\":%d.%d}", data[2], data[3],data[0],data[1]);
			parseSendRequest(client, "POST", "/1/classes/RpiData",string, NULL);
		}

		delay( DELAY ); 
	}
 
	return(0);
}

