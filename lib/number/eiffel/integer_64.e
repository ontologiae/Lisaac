-- See the Copyright notice at the end of this file.
--
expanded class INTEGER_64
	--
	-- 64 bits signed integer.
	--

insert
	INTEGER_GENERAL

feature {ANY} -- Conversions:
	fit_integer_8: BOOLEAN is
			-- Does `Current' fit in INTEGER_8?
		do
			if Current >= -128 then
				Result := Current <= 127
			end
		ensure
			Result = Current.in_range(-128, 127)
		end

	to_integer_8: INTEGER_8 is
			-- Explicit conversion to INTEGER_8.
		require
			fit_integer_8
		external "built_in"
		ensure
			Current.is_equal(Result)
		end

	fit_integer_16: BOOLEAN is
			-- Does `Current' fit in INTEGER_16?
		do
			if Current >= -32768 then
				Result := Current <= 32767
			end
		ensure
			Result = Current.in_range(-32768, 32767)
		end

	to_integer_16: INTEGER_16 is
			-- Explicit conversion to INTEGER_16.
		require
			fit_integer_16
		external "built_in"
		ensure
			Current.is_equal(Result)
		end

	fit_integer_32: BOOLEAN is
			-- Does `Current' fit in INTEGER_32?
		do
			if Current >= -2147483648 then
				Result := Current <= 2147483647
			end
		ensure
			Result = Current.in_range(-2147483648, 2147483647)
		end

	to_integer_32, to_integer: INTEGER_32 is
			-- Explicit conversion to INTEGER_32.
		require
			fit_integer_32
		external "built_in"
		ensure
			Current = Result
		end

	force_to_real_32: REAL_32 is
			-- Forced conversion to REAL_32 (possible loss of precision).
			-- (See also `fit_real_32' and `to_real_32'.)
		external "built_in"
		end

	fit_real_32: BOOLEAN is
			-- Does `Current' fit in REAL_32?
		do
			Result := fit_integer_32 and then to_integer_32.fit_real_32
		end

	to_real_32: REAL_32 is
			-- Explicit conversion to REAL_32. (See also `force_to_real_32'.)
		require
			fit_real_32
		do
			Result := force_to_real_32
		ensure
			Result.force_to_integer_64 = Current
		end

	force_to_real_64: REAL_64 is
			-- Forced conversion to REAL_64 (possible loss of precision).
			-- (See also `fit_real_64' and `to_real_64'.)
		external "built_in"
		end

	fit_real_64: BOOLEAN is
			-- Does `Current' fit in REAL_64?
		do
			Result := integer_64_fit_real_64(Current)
		end

	to_real_64: REAL_64 is
			-- Explicit conversion to REAL_64. (See also `force_to_real_64'.)
		require
			fit_real_64
		do
			Result := force_to_real_64
		ensure
			Result.force_to_integer_64 = Current
		end

	to_number: NUMBER is
		local
			number_tools: NUMBER_TOOLS
		do
			Result := number_tools.from_integer_64(Current)
		ensure
			Result @= Current
		end

	decimal_digit: CHARACTER is
		do
			Result := (Current.to_integer_32 + '0'.code).to_character
		end

	hexadecimal_digit: CHARACTER is
		do
			if Current <= 9 then
				Result := (to_integer_8 + '0'.code).to_character
			else
				Result := ('A'.code + (to_integer_8 - 10)).to_character
			end
		end

feature {ANY}
	low_32: INTEGER_32 is
			-- The 32 low bits of `Current' (i.e. the right-most part).
		external "built_in"
		end

	high_32: INTEGER_32 is
			-- The 32 high bits of `Current' (i.e. the left-most part).
		do
			Result := (Current |>> 32).low_32
		end

	one: INTEGER_8 is 1

	zero: INTEGER_8 is 0

	hash_code: INTEGER is
		do
			Result := Current.low_32 & 0x7FFFFFFF
		end

	sqrt: REAL is
		do
			Result := force_to_real_64.sqrt
		end

	log: REAL is
		do
			Result := force_to_real_64.log
		end

	log10: REAL is
		do
			Result := force_to_real_64.log10
		end

feature {}
	integer_64_fit_real_64 (integer_64: INTEGER_64): BOOLEAN is
		external "plug_in"
		alias "{
			location: "${sys}runtime"
			module_name: "integer_fit_real"
			feature_name: "integer_64_fit_real_64"
			}"
		end

end -- class INTEGER_64
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
