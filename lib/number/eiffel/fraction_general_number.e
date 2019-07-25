-- See the Copyright notice at the end of this file.
--
deferred class FRACTION_GENERAL_NUMBER
	--
	-- To implement NUMBER (do not use this class, see NUMBER).
	--

inherit
	NUMBER

feature {ANY}
	is_zero: BOOLEAN is False

	is_one: BOOLEAN is False

	is_positive: BOOLEAN is
		do
			Result := not is_negative
		end

	is_negative: BOOLEAN is
		do
			Result := numerator.is_negative
		end

	factorial: NUMBER is
		do
			check
				False
			end
		end

	infix "@=" (other: INTEGER_64): BOOLEAN is
		do
		end

	infix "//" (other: NUMBER): NUMBER is
		do
			check
				False
			end
		end

	infix "@//" (other: INTEGER_64): NUMBER is
		do
			check
				False
			end
		end

	infix "\\" (other: NUMBER): NUMBER is
		do
			check
				False
			end
		end

	infix "@\\" (other: INTEGER_64): NUMBER is
		do
			check
				False
			end
		end

feature {ANY} -- Misc:
	hash_code: INTEGER is
		do
			Result := numerator.hash_code #+ denominator.hash_code
			if Result < 0 then
				Result := ~Result
			end
		end

	gcd (other: NUMBER): INTEGER_GENERAL_NUMBER is
		do
			check
				False
			end
		end

feature {}
	decimal_in (buffer: STRING; num, denom: NUMBER; negative: BOOLEAN; digits: INTEGER; all_digits: BOOLEAN) is
		local
			n, div, remainder: NUMBER; counter: INTEGER
		do
			if is_negative then
				buffer.extend('-')
				n := -num
			else
				n := num
			end
			div := n // denom
			div.append_in(buffer)
			remainder := n \\ denom
			if digits > 0 then
				if all_digits or else not (remainder @= 0) then
					buffer.extend('.')
					from
						counter := 1
					until
						counter > digits or else remainder @= 0
					loop
						n := remainder @* 10
						;(n // denom).append_in(buffer)
						remainder := n \\ denom
						counter := counter + 1
					end
					if all_digits then
						from
						until
							counter > digits
						loop
							buffer.extend('0')
							counter := counter + 1
						end
					end
				end
			end
		end

feature {NUMBER} -- Implementation:
	gcd_with_integer_64_number (other: INTEGER_64_NUMBER): INTEGER_GENERAL_NUMBER is
		do
			check
				False
			end
		end

	gcd_with_big_integer_number (other: BIG_INTEGER_NUMBER): INTEGER_GENERAL_NUMBER is
		do
			check
				False
			end
		end

invariant
	denominator @>= 2
	not numerator.is_zero

end -- class FRACTION_GENERAL_NUMBER
--
-- ------------------------------------------------------------------------------------------------------------
-- Copyright notice below. Please read.
--
-- This file is part of the SmartEiffel standard library.
-- Copyright(C) 1994-2002: INRIA - LORIA (INRIA Lorraine) - ESIAL U.H.P.       - University of Nancy 1 - FRANCE
-- Copyright(C) 2003-2006: INRIA - LORIA (INRIA Lorraine) - I.U.T. Charlemagne - University of Nancy 2 - FRANCE
--
-- Authors: Dominique COLNET, Philippe RIBET, Cyril ADRIAN, Vincent CROIZIER, Frederic MERIZEN
--
-- Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
-- documentation files (the "Software"), to deal in the Software without restriction, including without
-- limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
-- the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
-- conditions:
--
-- The above copyright notice and this permission notice shall be included in all copies or substantial
-- portions of the Software.
--
-- THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
-- LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
-- EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN
-- AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
-- OR OTHER DEALINGS IN THE SOFTWARE.
--
-- http://SmartEiffel.loria.fr - SmartEiffel@loria.fr
-- ------------------------------------------------------------------------------------------------------------
