/* -*- mode: c -*-
 * $Id: sumcol-gcc.code,v 1.42 2007-05-19 00:42:43 igouy-guest Exp $
 * http://www.bagley.org/~doug/shootout/
 */

#include <stdio.h>
#include <stdlib.h>

#define MAXLINELEN 128

int
main() {
    int sum = 0;
    char line[MAXLINELEN];

    while (fgets(line, MAXLINELEN, stdin)) {
	sum += atoi(line);
    }
    printf("%d\n", sum);
    return(0);
}
