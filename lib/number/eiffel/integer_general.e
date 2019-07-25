-- See the Copyright notice at the end of this file.
--
deferred class INTEGER_GENERAL
	--
	-- General integer abstraction to share feature definition of INTEGER_8, INTEGER_16, INTEGER,
	-- INTEGER_32 and INTEGER_64. All implementation have a limited size and are supposed to use
	-- two's complement.
	--
	-- If you need integers with bigger values, use NUMBER or MUTABLE_BIG_INTEGER.
	--

inherit
	COMPARABLE
		undefine infix "<=", infix ">", infix ">="
		redefine fill_tagged_out_memory, out_in_tagged_out_memory, is_equal
		end
insert
	NUMERIC
		redefine fill_tagged_out_memory, out_in_tagged_out_memory
		end

feature {ANY}
	infix "+" (other: like Current): like Current is
		require
			no_overflow: Current > 0 = (other > 0) implies Current #+ other > 0 = (Current > 0) -- this means: if operand are of same sign, it will be sign of the Result.
		do
			Result := Current #+ other
		ensure
			Result #- other = Current
		end

	infix "-" (other: like Current): like Current is
		require
			no_overflow: Current > 0 /= (other > 0) implies Current #- other > 0 = (Current > 0) -- this means: if operand are of different sign, sign of the Result will be the same sign as Current.
		do
			Result := Current #- other
		ensure
			Result #+ other = Current
		end

	infix "*" (other: like Current): like Current is
		require
			no_overflow: divisible(other) implies Current #* other #// other = Current
		do
			Result := Current #* other
		ensure
			Current /= 0 and other /= 0 implies Result /= 0
			Result /= 0 implies Result #// other = Current
			Result /= 0 implies Result #\\ other = 0
		end

	infix "/" (other: like Current): REAL is
		external "built_in"
		end

	infix "//" (other: like Current): like Current is
			-- Quotient of the euclidian division of `Current' by `other'.
			-- The corresponding remainder is given by infix "\\".
			--
			-- See also infix "#//".
		require
			divisible(other)
			no_overflow: other = -1 implies Current = 0 or Current |<< 1 /= 0
		do
			if Current >= 0 then
				Result := Current #// other
			elseif other > 0 then
				Result := (Current + 1) #// other - 1
			else
				Result := (Current + 1) #// other + 1
			end
		ensure
			euclidian_divide_case1: Current >= 0 implies Result * other + Current \\ other = Current
			euclidian_divide_case2: Current < 0 implies Result #* other #+ (Current \\ other) = Current
		end

	infix "#//" (other: like Current): like Current is
			-- Integer division of `Current' by `other'.
			--
			-- According to the ANSI C99: if `Current' and `other' are both non-negative, the `Result' is the
			-- quotient of the euclidian division; but this is not the general case, the `Result' value is the
			-- algebraic quotient `Current/other' with any fractional part discarded. (This is often called
			-- "truncated toward zero"). So, the corresponding remainder value only verify the expression
			-- `remainder.abs < other.abs'.
			--
			-- See also infix "//", infix "#\\".
		require
			divisible(other)
		external "built_in"
		ensure
			Result * other + Current #\\ other = Current
			ansi_c_remainder: (Current - Result * other).abs < other.abs
			ansi_c_good_case: Current >= 0 and other > 0 implies Current - Result * other >= 0
		end

	infix "\\" (other: like Current): like Current is
			-- Remainder of the euclidian division of `Current' by `other'.
			-- By definition, `0 <= Result < other.abs'.
			--
			-- See also infix "#\\", infix "//".
		require
			divisible(other)
		do
			Result := Current #\\ other
			if Result < 0 then
				if other > 0 then
					Result := Result + other
				else
					Result := Result - other
				end
				check
					Result > 0
				end
			end
		ensure
			Result >= 0
			other |<< 1 /= 0 implies Result < other.abs
			good_remainder: Result #- (Current #\\ other) #\\ other = 0
		end

	infix "#\\" (other: like Current): like Current is
			-- Remainder of the integer division of `Current' by `other'.
			-- According to the ANSI C99:
			--   * if `Current' and `other' are both non-negative,
			--     the `Result' is the remainder of the euclidian division.
			--   * but this is not the general case,
			--     `Result' as the same sign as `Current' and only verify
			--     the expression `Result.abs < other.abs'.
			--
			-- See also infix "\\", infix "#//".
		require
			divisible(other)
		external "built_in"
		ensure
			(Current - Result) #\\ other = 0
			ansi_c_remainder: Result.abs < other.abs
			ansi_c_good_case: Current >= 0 and other > 0 implies Result >= 0
		end

	infix "^" (exp: like Current): INTEGER_64 is
			-- Integer power of `Current' by `other'
		require
			exp >= 0
		local
			i: like Current; k: INTEGER_64
		do
			i := exp
			if i = 0 then
				Result := 1
			else
				Result := Current
				from
					k := 1
				until
					i <= 1
				loop
					if i.is_odd then
						k := k * Result
					end
					Result := Result * Result
					i := i #// 2
				end
				Result := Result * k
			end
		end

	abs: like Current is
			-- Absolute value of `Current'.
		require
			not_minimum_value: Current = 0 or else Current |<< 1 /= 0
		do
			if Current < 0 then
				Result := -Current
			else
				Result := Current
			end
		ensure
			Result >= 0
		end

	infix "<" (other: like Current): BOOLEAN is
		external "built_in"
		end

	infix "<=" (other: like Current): BOOLEAN is
		external "built_in"
		end

	infix ">" (other: like Current): BOOLEAN is
		external "built_in"
		end

	infix ">=" (other: like Current): BOOLEAN is
		external "built_in"
		end

	prefix "+": like Current is
		do
			Result := Current
		end

	prefix "-": like Current is
		require
			not_minimum_value: Current < 0 implies 0 < #-Current
		do
			Result := #-Current
		end

	is_odd: BOOLEAN is
			-- Is odd?
		do
			Result := Current & 1 /= 0
		end

	is_even: BOOLEAN is
			-- Is even?
		do
			Result := Current & 1 = 0
		end

	sqrt: REAL is
			-- Square root of `Current'.
		require
			Current >= 0
		deferred
		end

	log: REAL is
			-- Natural Logarithm of `Current'.
		require
			Current > 0
		deferred
		end

	log10: REAL is
			-- Base-10 Logarithm of Current.
		require
			Current > 0
		deferred
		end

	gcd (other: like Current): like Current is
			-- Great Common Divisor of `Current' and `other'.
		do
			if other = 0 then
				Result := Current.abs
			else
				Result := other.gcd(Current \\ other)
			end
		ensure
			Result >= 0
			Result = 0 implies Current = 0 and other = 0
			Result >= 2 implies Current \\ Result = 0 and other \\ Result = 0 and (Current // Result).gcd(other // Result) = 1
		end

feature {ANY} -- Conversions:
	to_string: STRING is
			-- The decimal view of `Current' into a new allocated STRING.
			-- For example, if `Current' is -1 the `Result' is "-1".
			--
			-- See also `append_in', `to_string_format', `to_unicode_string', `to_hexadecimal', `to_octal'.
		do
			string_buffer.clear_count
			append_in(string_buffer)
			Result := string_buffer.twin
		end

	to_unicode_string: UNICODE_STRING is
			-- The decimal view of `Current' into a new allocated UNICODE_STRING.
			-- For example, if `Current' is -1 the `Result' is U"-1".
			--
			-- See also `append_in_unicode', `to_unicode_string_format', `to_string', `to_hexadecimal', `to_octal'.
		do
			unicode_string_buffer.clear_count
			append_in_unicode(unicode_string_buffer)
			Result := unicode_string_buffer.twin
		end

	to_boolean: BOOLEAN is
			-- Return False for 0, otherwise True.
			--
			-- See also `to_string', `to_character', `to_hexadecimal', `to_number'.
		do
			Result := Current /= 0
		ensure
			Result = (Current /= 0)
		end

	to_number: NUMBER is
			-- Convert `Current' into a new allocated NUMBER.
			--
			-- See also `to_boolean', `to_string', `to_character', `to_hexadecimal'.
		deferred
		end

	append_in (buffer: STRING) is
			-- Append in the `buffer' the equivalent of `to_string'.
			-- If you look for performances, you should always prefer `append_in' which allow you to recycle
			-- a unique common `buffer' (each call of `to_string' allocate a new object!).
			--
			-- See also `append_in_format', `append_in_unicode', `append_in_unicode_format', `to_hexadecimal_in'.
		require
			buffer /= Void
		local
			val: like Current; i, idx: INTEGER
		do
			if Current = 0 then
				buffer.extend('0')
			else
				from
					if Current > 0 then
						val := Current
						-- Save the position of first character in the buffer.
						i := buffer.count + 1
					else
						buffer.extend('-')
						-- Save the position of first character in the buffer.
						i := buffer.count + 1
						-- First (last) digit
						val := Current #\\ 10
						if val <= 0 then
							buffer.extend((-val).decimal_digit)
							val := -(Current #// 10)
						else
							buffer.extend((-val + 10).decimal_digit)
							val := -(Current #// 10) - 1
						end
						check
							val >= 0
						end
					end
				until
					val = 0
				loop
					buffer.extend((val #\\ 10).decimal_digit)
					val := val #// 10
				end
				-- Change character order.
				from
					idx := buffer.count
				until
					i >= idx
				loop
					buffer.swap(i, idx)
					idx := idx - 1
					i := i + 1
				end
			end
		end

	append_in_unicode (buffer: UNICODE_STRING) is
			-- Append in the `buffer' the equivalent of `to_unicode_string'.
			-- If you look for performances, you should always prefer `append_in_unicode' which allow you to recycle
			-- a unique common `buffer' (each call of `to_unicode_string' allocate a new object!).
			--
			-- See also `append_in_unicode_format', `append_in', `append_in_format', `to_hexadecimal_in'.
		require
			buffer /= Void
		local
			val: like Current; i, idx: INTEGER
		do
			if Current = 0 then
				buffer.extend('0'.code)
			else
				from
					if Current > 0 then
						val := Current
						-- Save the position of first character in the buffer.
						i := buffer.count + 1
					else
						buffer.extend('-'.code)
						-- Save the position of first character in the buffer.
						i := buffer.count + 1
						-- First (last) digit
						val := Current #\\ 10
						if val <= 0 then
							buffer.extend((-val).decimal_digit.code)
							val := -(Current #// 10)
						else
							buffer.extend((- val + 10).decimal_digit.code)
							val := -(Current #// 10) - 1
						end
						check
							val >= 0
						end
					end
				until
					val = 0
				loop
					buffer.extend((val #\\ 10).decimal_digit.code)
					val := val #// 10
				end
				-- Change character order.
				from
					idx := buffer.count
				until
					i >= idx
				loop
					buffer.swap(i, idx)
					idx := idx - 1
					i := i + 1
				end
			end
		end

	to_string_format (s: INTEGER): STRING is
			-- Same as `to_string' but the result is on `s' character and the number is right aligned.
			--
			-- See also `append_in_format', `to_character', `to_number', `to_hexadecimal'.
		require
			to_string.count <= s
		local
			i: INTEGER
		do
			string_buffer.clear_count
			append_in(string_buffer)
			from
				create Result.make(string_buffer.count.max(s))
				i := s - string_buffer.count
			until
				i <= 0
			loop
				Result.extend(' ')
				i := i - 1
			end
			Result.append(string_buffer)
		ensure
			Result.count = s
		end

	to_unicode_string_format (s: INTEGER): UNICODE_STRING is
			-- Same as `to_unicode_string' but the result is on `s' character and the number is right aligned.
			--
			-- See also `append_in_unicode_format', `to_string', `to_hexadecimal', `to_octal'.
		require
			to_string.count <= s
		local
			i: INTEGER
		do
			unicode_string_buffer.clear_count
			append_in_unicode(unicode_string_buffer)
			from
				create Result.make(unicode_string_buffer.count.max(s))
				i := s - unicode_string_buffer.count
			until
				i <= 0
			loop
				Result.extend(' '.code)
				i := i - 1
			end
			Result.append(unicode_string_buffer)
		ensure
			Result.count = s
		end

	append_in_format (buffer: STRING; s: INTEGER) is
			-- Append in the `buffer' the equivalent of `to_string_format'.
			-- If you look for performances, you should always prefer `append_in_format' which allow you to recycle
			-- a unique common `buffer' (each call of `to_string_format' allocate a new object!).
			--
			-- See also `append_in', `append_in_unicode', `append_in_unicode_format', `to_hexadecimal_in'.
		require
			to_string.count <= s
		local
			i: INTEGER
		do
			string_buffer.clear_count
			append_in(string_buffer)
			from
				i := s - string_buffer.count
			until
				i <= 0
			loop
				buffer.extend(' ')
				i := i - 1
			end
			buffer.append(string_buffer)
		ensure
			buffer.count >= old buffer.count + s
		end

	append_in_unicode_format (buffer: UNICODE_STRING; s: INTEGER) is
			-- Append in the `buffer' the equivalent of `to_unicode_string_format'.
			-- If you look for performances, you should always prefer `append_in_unicode_format' which allow you to recycle
			-- a unique common `buffer' (each call of `to_unicode_string_format' allocate a new object!).
			--
			-- See also `append_in_format', `append_in', `append_in_format', `to_hexadecimal_in'.
		require
			to_string.count <= s
		local
			i: INTEGER
		do
			unicode_string_buffer.clear_count
			append_in_unicode(unicode_string_buffer)
			from
				i := s - unicode_string_buffer.count
			until
				i <= 0
			loop
				buffer.extend(' '.code)
				i := i - 1
			end
			buffer.append(unicode_string_buffer)
		ensure
			buffer.count >= old buffer.count + s
		end

	digit: CHARACTER is
			-- Legacy synonym for `decimal_digit'.
			-- Note: already prefer `decimal_digit' because digit may become obsolete (feb 4th 2006).
		do
			Result := decimal_digit
		ensure
			Result = decimal_digit
		end

	decimal_digit: CHARACTER is
			-- Gives the corresponding CHARACTER for range 0..9.
		require
			in_range(0, 9)
		deferred
		ensure
			(once "0123456789").has(Result)
			Current.is_equal(Result.value)
		end

	hexadecimal_digit: CHARACTER is
			-- Gives the corresponding CHARACTER for range 0..15.
		require
			in_range(0, 15)
		deferred
		ensure
			(once "0123456789ABCDEF").has(Result)
		end

	to_character: CHARACTER is
			-- Return the coresponding ASCII character.
			--
			-- See also `to_boolean', `to_number', `to_string', `to_hexadecimal'.
		require
			Current >= 0
		external "built_in"
		end

	to_octal_in (buffer: STRING) is
			-- Append in the `buffer' the equivalent of `to_octal'.
			-- If you look for performances, you should always prefer `to_octal_in' which allow you to recycle
			-- a unique common `buffer' (each call of `to_octal' allocate a new object!).
			--
			-- See also `to_hexadecimal_in', `append_in', `append_in_format', `append_in_unicode'.
		local
			index, times: INTEGER; value: like Current
		do
			from
				value := Current
				times := (object_size * 8) #// 3 + 1
				index := buffer.count + times
				buffer.extend_multiple(' ', times)
			until
				times = 0
			loop
				buffer.put((value & 0x07).decimal_digit, index)
				index := index - 1
				value := value |>>> 3
				times := times - 1
			end
		ensure
			buffer.count = old buffer.count + (object_size * 8) #// 3 + 1
		end

	to_octal: STRING is
			-- The octal view of `Current' into a new allocated STRING.
			-- For example, if `Current' is -1 and if `Current' is a 16 bits integer the `Result' is "177777".
			--
			-- See also `to_octal_in', `to_hexadecimal', `to_number', `to_string'.
		do
			string_buffer.clear_count
			to_octal_in(string_buffer)
			Result := string_buffer.twin
		ensure
			Result.count = (object_size * 8) #// 3 + 1
		end

	to_hexadecimal: STRING is
			-- The hexadecimal view of `Current' into a new allocated STRING.
			-- For example, if `Current' is -1 and if `Current' is a 32 bits integer the `Result' is "FFFFFFFF".
			--
			-- See also `to_hexadecimal_in', `to_octal', `to_number', `to_string'.
		do
			string_buffer.clear_count
			to_hexadecimal_in(string_buffer)
			Result := string_buffer.twin
		ensure
			Result.count = object_size * 2
		end

	to_hexadecimal_in (buffer: STRING) is
			-- Append in the `buffer' the equivalent of `to_hexadecimal'.
			-- If you look for performances, you should always prefer `to_hexadecimal_in' which allow you to recycle
			-- a unique common `buffer' (each call of `to_hexadecimal' allocate a new object!).
			--
			-- See also `to_octal_in', `append_in', `append_in_format', `append_in_unicode'.
		local
			index, times: INTEGER; value: like Current
		do
			from
				value := Current
				times := object_size * 2
				index := buffer.count + times
				buffer.extend_multiple(' ', times)
			until
				times = 0
			loop
				buffer.put((value & 0x0F).hexadecimal_digit, index)
				index := index - 1
				value := value |>> 4
				times := times - 1
			end
		ensure
			buffer.count = old buffer.count + object_size * 2
		end

feature {ANY} -- Bitwise Logical Operators:
	bit_test (idx: INTEGER_8): BOOLEAN is
			-- The value of the `idx'-ith bit (the right-most bit is at
			-- index 0).
		require
			idx >= 0
			idx < object_size * 8
		do
			Result := Current.bit_shift_right(idx) & 1 /= 0
		end

	bit_set (idx: INTEGER_8): like Current is
			-- The value of the `idx'-ith bit (the right-most bit is at
			-- index 0).
		require
			idx >= 0
			idx < object_size * 8
		local
			mask: like Current
		do
			mask := 1
			Result := Current.bit_or(mask.bit_shift_left(idx))
		ensure
			Result.bit_test(idx)
			Result = Current or Result.bit_reset(idx) = Current
		end

	bit_reset (idx: INTEGER_8): like Current is
			-- The value of the `idx'-ith bit (the right-most bit is at
			-- index 0).
		require
			idx >= 0
			idx < object_size * 8
		local
			mask: like Current
		do
			mask := 1
			Result := Current.bit_and(mask.bit_shift_left(idx).bit_not)
		ensure
			not Result.bit_test(idx)
			Result = Current or Result.bit_set(idx) = Current
		end

	infix "|>>", bit_shift_right (s: INTEGER_8): like Current is
			-- Shift by `s' positions right (sign bit copied) bits falling
			-- off the end are lost.
		require
			s >= 0
		external "built_in"
		end

	infix "|>>>", bit_shift_right_unsigned (s: INTEGER_8): like Current is
			-- Shift by `s' positions right (sign bit not copied) bits
			-- falling off the end are lost.
		require
			s >= 0
		external "built_in"
		end

	infix "|<<", bit_shift_left (s: INTEGER_8): like Current is
			-- Shift by `s' positions left bits falling off the end are lost.
		require
			s >= 0
		external "built_in"
		end

	infix "#>>", bit_rotate_right (s: INTEGER_8): like Current is
			-- Rotate by `s' positions right.
		require
			s > 0
			s < object_size * 8
		external "built_in"
		end

	infix "#<<", bit_rotate_left (s: INTEGER_8): like Current is
			-- Rotate by `s' positions left.
		require
			s > 0
			s < object_size * 8
		external "built_in"
		end

	bit_rotate (s: INTEGER_8): like Current is
			-- Rotate by `s' positions (positive `s' shifts right, negative left
			--
			-- See also infix "#>>" and infix "#<<".
		require
			s > -object_size * 8
			s < object_size * 8
		external "built_in"
		end

	prefix "~", bit_not: like Current is
			-- One's complement of `Current'.
		external "built_in"
		end

	infix "&", bit_and (other: like Current): like Current is
			-- Bitwise logical and of `Current' with `other'.
		external "built_in"
		end

	infix "|", bit_or (other: like Current): like Current is
			-- Bitwise logical inclusive or of `Current' with `other'.
		external "built_in"
		end

	bit_xor (other: like Current): like Current is
			-- Bitwise logical exclusive or of `Current' with `other'.
		external "built_in"
		end

	bit_shift (s: INTEGER_8): like Current is
			-- Shift by `s' positions (positive `s' shifts right (sign bit
			-- copied), negative shifts left bits falling off the end are lost).
			--
			-- See also infix "|>>" and infix "|<<".
		require
			s /= 0
		do
			if s > 0 then
				Result := Current |>> s
			else
				Result := Current |<< -s
			end
		end

	bit_shift_unsigned (s: INTEGER_8): like Current is
			-- Shift by `s' positions (positive `s' shifts right (sign bit not
			-- copied), negative left bits falling off the end are lost).
			--
			-- See also infix "|>>>" and infix "|<<".
		require
			s /= 0
		do
			if s > 0 then
				Result := Current |>>> s
			else
				Result := Current |<< -s
			end
		end

feature {ANY} -- Object Printing:
	out_in_tagged_out_memory, fill_tagged_out_memory is
		do
			Current.append_in(tagged_out_memory)
		end

feature {ANY} -- Miscellaneous:
	sign: INTEGER_8 is
			-- Sign of `Current' (0 or -1 or 1).
		do
			if Current < 0 then
				Result := -1
			elseif Current > 0 then
				Result := 1
			end
		end

	divisible (other: like Current): BOOLEAN is
		do
			Result := other /= 0
		end

	is_equal (other: like Current): BOOLEAN is
		do
			Result := Current = other
		end

	is_a_power_of_2: BOOLEAN is
			-- Is `Current' a power of 2?
		require
			Current > 0
		do
			Result := (Current - 1) & Current = 0
		end

feature {ANY} -- For experts only: native operators with potential untrapped overflow (no protection):
	infix "#+" (other: like Current): like Current is
		external "built_in"
		end

	prefix "#-": like Current is
		external "built_in"
		end

	infix "#-" (other: like Current): like Current is
		external "built_in"
		end

	infix "#*" (other: like Current): like Current is
		external "built_in"
		end

feature {}
	string_buffer: STRING is
		once
			create Result.make(128)
		end

	unicode_string_buffer: UNICODE_STRING is
		once
			create Result.make(128)
		end

end -- class INTEGER_GENERAL
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
