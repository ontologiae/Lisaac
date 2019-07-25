-- See the Copyright notice at the end of this file.
--
class INTEGER_64_NUMBER
	--
	-- To implement NUMBER (do not use this class, see NUMBER).
	--
	-- This is an INTEGER_64

inherit
	INTEGER_GENERAL_NUMBER

creation {ANY}
	make

feature {ANY}
	is_zero: BOOLEAN is
		do
			Result := value = 0
		end

	is_one: BOOLEAN is
		do
			Result := value = 1
		end

	is_positive: BOOLEAN is
		do
			Result := value > 0
		end

	is_negative: BOOLEAN is
		do
			Result := value < 0
		end

	is_integer_value: BOOLEAN is
		do
			Result := True
		end

	force_to_real_64: REAL_64 is
		do
			Result := value.force_to_real_64
		end

	append_in (buffer: STRING) is
		do
			buffer.append(value.to_string)
		end

	append_in_unicode (buffer: UNICODE_STRING) is
		do
			buffer.append(value.to_unicode_string)
		end

	prefix "-": INTEGER_GENERAL_NUMBER is
		do
			if value = Minimum_integer_64 then
				mutable_register1.from_integer_64(Minimum_integer_64)
				mutable_register1.negate
				Result := mutable_register1.to_integer_general_number
			else
				create {INTEGER_64_NUMBER} Result.make(-value)
			end
		end

	infix "+" (other: NUMBER): NUMBER is
		do
			Result := other @+ value
		end

	infix "@+" (other: INTEGER_64): NUMBER is
		local
			sum: INTEGER_64
		do
			sum := value #+ other
			if value < 0 = (other < 0) implies sum < 0 = (value < 0) then
				-- no overflow
				create {INTEGER_64_NUMBER} Result.make(sum)
			else
				mutable_register1.from_integer_64(value)
				mutable_register1.add_integer_64(other)
				Result := mutable_register1.to_integer_general_number
			end
		end

	infix "*" (other: NUMBER): NUMBER is
		do
			if is_zero then
				Result := zero
			else
				Result := other @* value
			end
		end

	infix "@*" (other: INTEGER_64): NUMBER is
		do
			if other = 0 or else value = 0 then
				create {INTEGER_64_NUMBER} Result.make(0)
			elseif other = 1 then
				Result := Current
			elseif value = 1 then
				create {INTEGER_64_NUMBER} Result.make(other)
			else
				mutable_register1.from_integer_64(value)
				mutable_register2.from_integer_64(other)
				mutable_register1.multiply_to(mutable_register2, mutable_register3)
				Result := mutable_register3.to_integer_general_number
			end
		end

	infix "@/" (other: INTEGER_64): NUMBER is
		local
			n, other_number: INTEGER_64_NUMBER; g: INTEGER_64
		do
			if value = 0 or else other = 1 then
				Result := Current
			elseif other = -1 then
				if value = Minimum_integer_64 then
					mutable_register1.from_integer_64(value)
					mutable_register1.negate
					Result := mutable_register1.to_integer_general_number
				else
					create {INTEGER_64_NUMBER} Result.make(-value)
				end
			else
				g := value.gcd(other)
				check
					g /= 0
				end
				if g = 1 then
					create other_number.make(other)
					if other < 0 then
						create {FRACTION_WITH_BIG_INTEGER_NUMBER} Result.make_simply(-Current, -other_number)
					else
						create {FRACTION_WITH_BIG_INTEGER_NUMBER} Result.make_simply(Current, other_number)
					end
				elseif other = g or else other = -g then
					Result := (value // other).to_number
				elseif other < 0 then
					create other_number.make(-(other // g))
					create n.make(-(value // g))
					create {FRACTION_WITH_BIG_INTEGER_NUMBER} Result.make_simply(n, other_number)
				else
					create other_number.make(other // g)
					create n.make(value // g)
					create {FRACTION_WITH_BIG_INTEGER_NUMBER} Result.make_simply(n, other_number)
				end
			end
		end

	infix "//" (other: NUMBER): NUMBER is
		local
			oth: INTEGER_GENERAL_NUMBER
		do
			oth ::= other
			Result := oth.integer_divide_integer_64_number(Current)
		end

	infix "@//" (other: INTEGER_64): NUMBER is
		do
			--|*** Must be rewrited directly with integer_64 (Vincent Croizier, 04/07/04) ***
			put_into_mutable_big_integer(mutable_register1)
			mutable_register2.from_integer_64(other)
			mutable_register1.divide_to(mutable_register2, mutable_register3, mutable_register4)
			Result := mutable_register3.to_integer_general_number
		end

	infix "\\" (other: NUMBER): NUMBER is
		local
			oth: INTEGER_GENERAL_NUMBER
		do
			oth ::= other
			Result := oth.remainder_of_divide_integer_64_number(Current)
		end

	infix "@\\" (other: INTEGER_64): NUMBER is
		do
			--|*** Must be rewrited directly with integer_64 (Vincent Croizier, 04/07/04) ***
			put_into_mutable_big_integer(mutable_register1)
			mutable_register2.from_integer_64(other)
			mutable_register1.divide_to(mutable_register2, mutable_register3, mutable_register4)
			Result := mutable_register4.to_integer_general_number
		end

feature {ANY} -- Misc:
	hash_code: INTEGER is
		do
			Result := value.hash_code
		end

	gcd (other: NUMBER): INTEGER_GENERAL_NUMBER is
		do
			Result := other.gcd_with_integer_64_number(Current)
		end

	infix "@=" (other: INTEGER_64): BOOLEAN is
		do
			Result := value = other
		end

	infix "@<" (other: INTEGER_64): BOOLEAN is
		do
			Result := value < other
		end

	infix "@<=" (other: INTEGER_64): BOOLEAN is
		do
			Result := value <= other
		end

	infix "@>" (other: INTEGER_64): BOOLEAN is
		do
			Result := value > other
		end

	infix "@>=" (other: INTEGER_64): BOOLEAN is
		do
			Result := value >= other
		end

	infix "#=" (other: REAL_64): BOOLEAN is
		do
			--|*** Vincent, can you check ? *** (Dom Oct 2004) ***
			Result := value.force_to_real_64 = other
		end

	infix "#<" (other: REAL_64): BOOLEAN is
		do
			--|*** Vincent, can you check ? *** (Dom Oct 2004) ***
			Result := value.force_to_real_64 < other
		end

	infix "#<=" (other: REAL_64): BOOLEAN is
		do
			Result := value.force_to_real_64 <= other
		end

	infix "#>" (other: REAL_64): BOOLEAN is
		do
			Result := value.force_to_real_64 > other
			--|*** Vincent, can you check ? *** (Dom Oct 2004) ***
		end

	infix "#>=" (other: REAL_64): BOOLEAN is
		do
			Result := value.force_to_real_64 >= other
			--|*** Vincent, can you check ? *** (Dom Oct 2004) ***
		end

	infix "<" (other: NUMBER): BOOLEAN is
		do
			Result := other @> value
		end

	is_equal (other: NUMBER): BOOLEAN is
		local
			n2: like Current
		do
			if n2 ?:= other then
				n2 ::= other
				Result := value = n2.value
			end
		end

	inverse: NUMBER is
		do
			if is_one or else Current @= -1 then
				Result := Current
			elseif is_negative then
				create {FRACTION_WITH_BIG_INTEGER_NUMBER} Result.make_simply(integer_general_number_one_negative, -Current)
			else
				create {FRACTION_WITH_BIG_INTEGER_NUMBER} Result.make_simply(integer_general_number_one, Current)
			end
		end

feature {NUMBER} -- Implementation:
	value: INTEGER_64

	add_with_big_integer_number (other: BIG_INTEGER_NUMBER): NUMBER is
		do
			Result := other @+ value
		end

	add_with_fraction_with_big_integer_number (other: FRACTION_WITH_BIG_INTEGER_NUMBER): NUMBER is
		do
			Result := other @+ value
		end

	multiply_with_big_integer_number (other: BIG_INTEGER_NUMBER): NUMBER is
		do
			Result := other @* value
		end

	multiply_with_fraction_with_big_integer_number (other: FRACTION_WITH_BIG_INTEGER_NUMBER): NUMBER is
		do
			Result := other.multiply_with_integer_64_number(Current)
		end

	integer_divide_integer_64_number (other: INTEGER_64_NUMBER): INTEGER_GENERAL_NUMBER is
		do
			Result ::= other @// value
		end

	integer_divide_big_integer_number (other: BIG_INTEGER_NUMBER): INTEGER_GENERAL_NUMBER is
		do
			Result ::= other @// value
		end

	remainder_of_divide_integer_64_number (other: INTEGER_64_NUMBER): INTEGER_GENERAL_NUMBER is
		do
			Result ::= other @\\ value
		end

	remainder_of_divide_big_integer_number (other: BIG_INTEGER_NUMBER): INTEGER_GENERAL_NUMBER is
		do
			Result ::= other @\\ value
		end

	greater_with_big_integer_number (other: BIG_INTEGER_NUMBER): BOOLEAN is
		do
			Result := other.is_negative
		end

	greater_with_fraction_with_big_integer_number (other: FRACTION_WITH_BIG_INTEGER_NUMBER): BOOLEAN is
		do
			Result := other.denominator * Current > other.numerator
		end

feature {NUMBER} -- Implementation:
	gcd_with_integer_64_number (other: INTEGER_64_NUMBER): INTEGER_64_NUMBER is
		do
			create Result.make(value.gcd(other.value))
		end

feature {NUMBER}
	put_into_mutable_big_integer (mut: MUTABLE_BIG_INTEGER) is
		do
			mut.from_integer_64(value)
		end

feature {}
	make (val: INTEGER_64) is
		do
			value := val
		ensure
			Current.to_integer_64 = val
		end

end -- class INTEGER_64_NUMBER
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
