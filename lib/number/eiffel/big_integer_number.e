-- See the Copyright notice at the end of this file.
--
class BIG_INTEGER_NUMBER
	--
	-- To implement NUMBER (do not use this class, see NUMBER).
	--

inherit
	INTEGER_GENERAL_NUMBER

creation {ANY}
	from_native_array

feature {ANY}
	is_positive: BOOLEAN is
		do
			Result := not negative
		end

	is_negative: BOOLEAN is
		do
			Result := negative
		end

	infix "\\" (other: NUMBER): NUMBER is
		local
			oth: INTEGER_GENERAL_NUMBER
		do
			oth ::= other
			Result := oth.remainder_of_divide_big_integer_number(Current)
		end

	infix "@\\" (other: INTEGER_64): NUMBER is
		do
			put_into_mutable_big_integer(mutable_register1)
			mutable_register2.from_integer_64(other)
			mutable_register1.divide_to(mutable_register2, mutable_register3, mutable_register4)
			Result := mutable_register4.to_integer_general_number
		end

feature {ANY} -- Misc:
	hash_code: INTEGER is
		local
			i, c, v: INTEGER
		do
			from
				c := 2
			until
				c = 0 or else i = capacity
			loop
				v := storage.item(i)
				if v /= 0 then
					Result := Result #+ v
					c := c - 1
				end
				i := i + 1
			end
			Result := capacity #* Result
			if negative then
				Result := Result #+ 1
			end
			if Result < 0 then
				Result := ~Result
			end
		end

	gcd (other: NUMBER): INTEGER_GENERAL_NUMBER is
		do
			Result := other.gcd_with_big_integer_number(Current)
		end

	is_integer_value: BOOLEAN is False

	force_to_real_64: REAL_64 is
		do
			put_into_mutable_big_integer(mutable_register1)
			Result := mutable_register1.force_to_real_64
		end

	prefix "-": NUMBER is
		do
			if is_positive and then capacity = 2 and then storage.item(0) = 0 and then storage.item(1) = Minimum_integer then
				create {INTEGER_64_NUMBER} Result.make(Minimum_integer_64)
			else
				create {BIG_INTEGER_NUMBER} Result.from_native_array(storage, capacity, not negative)
			end
		end

	infix "+" (other: NUMBER): NUMBER is
		do
			Result := other.add_with_big_integer_number(Current)
		end

	infix "//" (other: NUMBER): NUMBER is
		local
			oth: INTEGER_GENERAL_NUMBER
		do
			oth ::= other
			Result := oth.integer_divide_big_integer_number(Current)
		end

	infix "@//" (other: INTEGER_64): NUMBER is
		do
			put_into_mutable_big_integer(mutable_register1)
			mutable_register2.from_integer_64(other)
			mutable_register1.divide_to(mutable_register2, mutable_register3, mutable_register4)
			Result := mutable_register3.to_integer_general_number
		end

	infix "*" (other: NUMBER): NUMBER is
		do
			Result := other.multiply_with_big_integer_number(Current)
		end

	infix "@/" (other: INTEGER_64): NUMBER is
		do
			Result := from_integer_general_number_and_integer(Current, other)
		end

	infix "@+" (other: INTEGER_64): NUMBER is
		do
			put_into_mutable_big_integer(mutable_register1)
			mutable_register1.add_integer_64(other)
			Result := mutable_register1.to_integer_general_number
		end

	infix "@*" (other: INTEGER_64): NUMBER is
		do
			if other = 0 then
				create {INTEGER_64_NUMBER} Result.make(0)
			elseif other = 1 then
				Result := Current
			elseif other = -1 then
				Result := -Current
			else
				put_into_mutable_big_integer(mutable_register1)
				mutable_register2.from_integer_64(other)
				mutable_register1.multiply_to(mutable_register2, mutable_register3)
				Result := mutable_register3.to_integer_general_number
			end
		end

feature {NUMBER}
	add_with_big_integer_number (other: BIG_INTEGER_NUMBER): NUMBER is
		do
			put_into_mutable_big_integer(mutable_register1)
			other.put_into_mutable_big_integer(mutable_register2)
			mutable_register1.add(mutable_register2)
			Result := mutable_register1.to_integer_general_number
		end

	add_with_fraction_with_big_integer_number (other: FRACTION_WITH_BIG_INTEGER_NUMBER): NUMBER is
		do
			Result := other.add_with_big_integer_number(Current)
		end

feature {NUMBER} -- Multiplication
	multiply_with_big_integer_number (other: BIG_INTEGER_NUMBER): NUMBER is
		do
			put_into_mutable_big_integer(mutable_register1)
			other.put_into_mutable_big_integer(mutable_register2)
			mutable_register1.multiply_to(mutable_register2, mutable_register3)
			Result := mutable_register3.to_integer_general_number
		end

	multiply_with_fraction_with_big_integer_number (other: FRACTION_WITH_BIG_INTEGER_NUMBER): NUMBER is
		do
			Result := other.multiply_with_big_integer_number(Current)
		end

feature {NUMBER} -- division
	integer_divide_integer_64_number (other: INTEGER_64_NUMBER): INTEGER_GENERAL_NUMBER is
		do
			if other @= Minimum_integer_64 and then is_positive and then capacity = 2 and then storage.item(0) = 0 and then storage.item(1) = Minimum_integer then
				Result := integer_general_number_one_negative
			else
				Result := integer_general_number_zero
			end
		end

	remainder_of_divide_integer_64_number (other: INTEGER_64_NUMBER): INTEGER_GENERAL_NUMBER is
		do
			if other @= Minimum_integer_64 and then is_positive and then capacity = 2 and then storage.item(0) = 0 and then storage.item(1) = Minimum_integer then
				Result := integer_general_number_zero
			else
				Result := other
			end
		end

	integer_divide_big_integer_number (other: BIG_INTEGER_NUMBER): INTEGER_GENERAL_NUMBER is
		do
			put_into_mutable_big_integer(mutable_register2)
			other.put_into_mutable_big_integer(mutable_register1)
			mutable_register1.divide_to(mutable_register2, mutable_register3, mutable_register4)
			Result := mutable_register3.to_integer_general_number
		end

	remainder_of_divide_big_integer_number (other: BIG_INTEGER_NUMBER): INTEGER_GENERAL_NUMBER is
		do
			put_into_mutable_big_integer(mutable_register2)
			other.put_into_mutable_big_integer(mutable_register1)
			mutable_register1.divide_to(mutable_register2, mutable_register3, mutable_register4)
			Result := mutable_register4.to_integer_general_number
		end

feature {NUMBER} -- Implementation:
	gcd_with_integer_64_number (other: INTEGER_64_NUMBER): INTEGER_GENERAL_NUMBER is
		do
			put_into_mutable_big_integer(mutable_register1)
			other.put_into_mutable_big_integer(mutable_register2)
			mutable_register1.gcd(mutable_register2)
			Result := mutable_register1.to_integer_general_number
		end

feature {ANY}
	inverse: NUMBER is
		local
			tmp: INTEGER_GENERAL_NUMBER
		do
			if is_negative then
				tmp ::= -Current
				create {FRACTION_WITH_BIG_INTEGER_NUMBER} Result.make(integer_general_number_one_negative, tmp)
			else
				create {FRACTION_WITH_BIG_INTEGER_NUMBER} Result.make(integer_general_number_one, Current)
			end
		end

	infix "@=" (other: INTEGER_64): BOOLEAN is
		do
		end

	infix "@<" (other: INTEGER_64): BOOLEAN is
		do
			Result := negative
		end

	infix "@>" (other: INTEGER_64): BOOLEAN is
		do
			Result := not negative
		end

	infix "@>=" (other: INTEGER_64): BOOLEAN is
		do
			Result := not negative
		end

	infix "@<=" (other: INTEGER_64): BOOLEAN is
		do
			Result := negative
		end

feature {ANY} -- comparisons with NUMBER
	infix "<" (other: NUMBER): BOOLEAN is
		do
			Result := other.greater_with_big_integer_number(Current)
		end

feature {ANY} -- comparisons with REAL_64
	infix "#=" (other: REAL_64): BOOLEAN is
		do
			if is_negative then
				if other >= 0 then
				elseif other >= Minimum_integer_64.force_to_real_64 then
					--|*** Vincent, can you check ? *** (Dom Oct 2004) ***
				elseif Current < min_double then
				else
					Result := force_to_real_64 = other
				end
			elseif other < 0 then
			elseif other <= Maximum_integer_64.force_to_real_64 then
				--|*** Vincent, can you check ? *** (Dom Oct 2004) ***
			elseif Current > max_double then
			else
				Result := force_to_real_64 = other
			end
		end

	infix "#<" (other: REAL_64): BOOLEAN is
		do
			if is_negative then
				if other >= 0 then
					Result := True
				elseif other >= Minimum_integer_64.force_to_real_64 then
					--|*** Vincent, can you check ? *** (Dom Oct 2004) ***
					Result := True
				elseif Current < min_double then
					Result := True
				else
					Result := force_to_real_64 < other
				end
			elseif other < 0 then
			elseif other <= Maximum_integer_64.force_to_real_64 then
				--|*** Vincent, can you check ? *** (Dom Oct 2004) ***
			elseif Current > max_double then
			else
				Result := force_to_real_64 < other
			end
		end

	infix "#<=" (other: REAL_64): BOOLEAN is
		do
			if is_negative then
				if other >= 0 then
					Result := True
				elseif other >= Minimum_integer_64.force_to_real_64 then
					--|*** Vincent, can you check ? *** (Dom Oct 2004) ***
					Result := True
				elseif Current <= min_double then
					Result := True
				else
					Result := force_to_real_64 <= other
				end
			elseif other <= 0 then
			elseif other <= Maximum_integer_64.force_to_real_64 then
				--|*** Vincent, can you check ? *** (Dom Oct 2004) ***
			elseif Current >= max_double then
			else
				Result := force_to_real_64 <= other
			end
		end

	infix "#>" (other: REAL_64): BOOLEAN is
		do
			if is_negative then
				if other >= 0 then
				elseif other >= Minimum_integer_64.force_to_real_64 then
					--|*** Vincent, can you check ? *** (Dom Oct 2004) ***
				elseif Current < min_double then
				else
					Result := force_to_real_64 > other
				end
			elseif other < 0 then
				Result := True
			elseif other <= Maximum_integer_64.force_to_real_64 then
				--|*** Vincent, can you check ? *** (Dom Oct 2004) ***
				Result := True
			elseif Current > max_double then
				Result := True
			else
				Result := force_to_real_64 > other
			end
		end

	infix "#>=" (other: REAL_64): BOOLEAN is
		do
			if is_negative then
				if other >= 0 then
				elseif other >= Minimum_integer_64.force_to_real_64 then
					--|*** Vincent, can you check ? *** (Dom Oct 2004) ***
				elseif Current <= min_double then
				else
					Result := force_to_real_64 >= other
				end
			elseif other <= 0 then
				Result := True
			elseif other <= Maximum_integer_64.force_to_real_64 then
				--|*** Vincent, can you check ? *** (Dom Oct 2004) ***
				Result := True
			elseif Current >= max_double then
				Result := True
			else
				Result := force_to_real_64 >= other
			end
		end

feature {NUMBER, NUMBER_TOOLS}
	greater_with_big_integer_number (other: BIG_INTEGER_NUMBER): BOOLEAN is
		local
			i, other_capacity: INTEGER; other_storage: like storage
		do
			if is_negative /= other.is_negative then
				Result := is_positive
			else
				other_capacity := other.value_length
				if capacity = other_capacity then
					from
						other_storage := other.value
						i := capacity - 1
					variant
						i
					until
						i < 0 or else storage.item(i) /= other_storage.item(i)
					loop
						i := i - 1
					end
					Result := i >= 0 and then (storage.item(i) > other_storage.item(i) xor is_negative)
				else
					Result := capacity > other_capacity xor is_negative
				end
			end
		end

	greater_with_fraction_with_big_integer_number (other: FRACTION_WITH_BIG_INTEGER_NUMBER): BOOLEAN is
		do
			Result := not other.greater_with_big_integer_number(Current)
		end

feature {ANY}
	is_zero: BOOLEAN is False

	is_one: BOOLEAN is False

	is_equal (other: NUMBER): BOOLEAN is
			-- Compares two LARGE_INTEGERs. As they both have same sign
			-- comparison is done with absolute values.
		local
			i: INTEGER; n2: like Current; n2_storage: like storage
		do
			if n2 ?:= other then
				n2 ::= other
				if is_negative = n2.is_negative then
					if capacity = n2.value_length then
						from
							n2_storage := n2.value
							i := capacity - 1
							Result := True
						until
							not Result or else i < 0
						loop
							Result := storage.item(i) = n2_storage.item(i)
							i := i - 1
						end
					end
				end
			end
		end

	append_in (buffer: STRING) is
		do
			put_into_mutable_big_integer(mutable_register1)
			mutable_register1.append_in(buffer)
		end

	append_in_unicode (buffer: UNICODE_STRING) is
		do
			put_into_mutable_big_integer(mutable_register1)
			mutable_register1.append_in_unicode(buffer)
		end

feature {NUMBER}
	value: NATIVE_ARRAY[INTEGER] is
		do
			Result := storage
		ensure
			Result = storage
		end

	value_length: INTEGER is
		do
			Result := capacity
		ensure
			Result = capacity
		end

feature {MUTABLE_BIG_INTEGER}
	from_native_array (na: NATIVE_ARRAY[INTEGER]; cap: INTEGER; neg: BOOLEAN) is
		require
		--na.item(cap - 1) /= 0 --|*** To be completed (class invariant) (Vincent Croizier, 02/07/04) ***
		do
			capacity := cap
			storage := na
			negative := neg
		ensure
			capacity = cap
			storage = na
			negative = neg
		end

feature {NUMBER}
	put_into_mutable_big_integer (mut: MUTABLE_BIG_INTEGER) is
		do
			mut.from_native_array(storage, capacity, negative)
		end

feature {}
	negative: BOOLEAN

	capacity: INTEGER

	storage: NATIVE_ARRAY[INTEGER]

invariant
	capacity >= 2
	storage.item(capacity - 1) /= 0
	not negative implies Current @> Maximum_integer_64
	negative implies Current @< Minimum_integer_64

end -- class BIG_INTEGER_NUMBER
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
