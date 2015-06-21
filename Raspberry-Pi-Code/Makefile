CC = gcc
SRCS = TemperatureMonitor.c
OBJS = $(SRCS:.c=.o)
INCLUDES =
LIBDIRS =
LIBS = -lwiringPi -lparse 
TARGET = tm

all: $(TARGET)

$(TARGET): $(OBJS) 
	$(CC) $(LIBDIRS) $(OBJS) -o $(TARGET) $(LIBS)

.c.o:
	$(CC) $(CFLAGS) $(INCLUDES) -c $< -o $@

clean:
	rm -fR $(TARGET) $(OBJS)

